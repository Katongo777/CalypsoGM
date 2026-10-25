package puppy.code.vista;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.modelo.Bullet;
import puppy.code.modelo.Nave4;
import puppy.code.modelo.Personaje;
import puppy.code.modelo.armas.BFG;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.enemigos.EnemigoBot;
import puppy.code.modelo.enemigos.EnemigoCheater;
import puppy.code.modelo.enemigos.EnemigoLag;
import puppy.code.modelo.niveles.DirectorNiveles;
import puppy.code.modelo.niveles.Nivel;
import puppy.code.modelo.niveles.NivelPartidaBuilder;
import puppy.code.modelo.powerups.Pizza;
import puppy.code.modelo.powerups.PowerUp;
import puppy.code.modelo.powerups.RGB;
import puppy.code.modelo.powerups.SillaGamer;
import puppy.code.util.GestorRecursos;

/*
    Pantalla donde se juega. Hereda de PantallaBase (Template Method), así que
    solo define los tres pasos: actualizar (lógica), dibujar y revisarTransicion.

    Ya no recibe 7 parámetros sueltos: recibe el Nivel (construido con el patrón
    Builder), la nave del jugador y el puntaje acumulado.
*/
public class PantallaJuego extends PantallaBase
{
	private Sound explosionSound;
	private Music gameMusic;
	private int score;
	private Nivel nivel;

	private Nave4 nave;
	private Locutor locutor;
	private Hud hud = new Hud();
	private boolean pausado = false;
	// Una sola lista para todos los enemigos
	private ArrayList<Enemigo> enemigos = new ArrayList<>();
	private ArrayList<Bullet> balas = new ArrayList<>();
	private ArrayList<PowerUp> powerUps = new ArrayList<>();

	// Arma una partida nueva: oleada 1, la nave del personaje elegido y puntaje 0
	public static PantallaJuego nuevaPartida(SpaceNavigation game, Personaje personaje)
	{
		Nave4 nave = new Nave4(personaje);
		if (game.isModoDios())
		{
			nave.setVidas(30); // easter egg del código Konami: 30 vidas y el BFG 9000
			nave.setEstrategia(new BFG());
		}

		// El Director usa el Builder para construir la primera oleada
		DirectorNiveles director = new DirectorNiveles(new NivelPartidaBuilder());
		return new PantallaJuego(game, director.construirOleada(1), nave, 0, new Locutor());
	}

	public PantallaJuego(SpaceNavigation game, Nivel nivel, Nave4 nave, int score, Locutor locutor)
	{
		super(game);
		this.nivel = nivel;
		this.nave = nave;
		this.score = score;
		this.locutor = locutor;

		// Los recursos vienen del Singleton: se cargan una sola vez y se reutilizan
		GestorRecursos recursos = GestorRecursos.getInstance();
		explosionSound = recursos.getSonido("explosion.ogg");
		gameMusic = recursos.getMusica();
		gameMusic.play();

		nave.reubicar();
		crearEnemigos(recursos);
	}

	// Crea los enemigos que pide el Nivel (cuántos de cada tipo y a qué velocidad)
	private void crearEnemigos(GestorRecursos recursos)
	{
		Random r = new Random();
		for (int i = 0; i < nivel.getCantBots(); i++)
		{
			enemigos.add(new EnemigoBot(posX(r), posY(r), velocidad(r), velocidad(r), recursos.getTextura("bot.png")));
		}
		for (int i = 0; i < nivel.getCantLags(); i++)
		{
			enemigos.add(new EnemigoLag(posX(r), posY(r), velocidad(r) * 1.5f, velocidad(r) * 1.5f,
					recursos.getTextura("lag.png")));
		}
		for (int i = 0; i < nivel.getCantCheaters(); i++)
		{
			enemigos.add(new EnemigoCheater(posX(r), posY(r), velocidad(r) * 0.6f, velocidad(r) * 0.6f,
					recursos.getTextura("cheater.png")));
		}
	}

	// Posiciones al azar (siempre lejos de la nave, que parte abajo)
	private int posX(Random r)
	{
		return r.nextInt(Constantes.ANCHO);
	}

	private int posY(Random r)
	{
		return 250 + r.nextInt(Constantes.ALTO - 250);
	}

	// Velocidad al azar (entre 1 y 4, escalada por la dificultad del nivel) y con dirección al azar
	private float velocidad(Random r)
	{
		float v = (1 + r.nextInt(4)) * nivel.getVelocidad();
		return r.nextBoolean() ? v : -v;
	}

	// ---------- PASO 1 del template: actualizar la lógica ----------
	@Override
	protected void actualizar(float delta)
	{
		// P pausa y reanuda el juego
		if (Gdx.input.isKeyJustPressed(Input.Keys.P))
		{
			pausado = !pausado;
		}
		if (pausado)
		{
			return;
		}

		locutor.actualizar();
		nave.actualizar();

		// Mantener el espacio apretado dispara (la nave controla la cadencia con su cooldown)
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
		{
			balas.addAll(nave.disparar());
		}

		// colisiones entre balas y enemigos y su destrucción
		for (int i = 0; i < balas.size(); i++)
		{
			Bullet b = balas.get(i);
			b.actualizar();
			for (int j = 0; j < enemigos.size(); j++)
			{
				Enemigo enemigo = enemigos.get(j);
				if (b.revisarColision(enemigo))
				{
					// El enemigo pierde vida; solo muere cuando se le acaba
					enemigo.recibirDano(b.getDanio());
					if (enemigo.estaMuerto())
					{
						explosionSound.play();
						score += enemigo.getPuntos();
						locutor.registrarEliminacion();
						enemigos.remove(j);
						j--;
						enemigos.addAll(enemigo.alMorir()); // por ejemplo, el Cheater suelta 2 Lags
						soltarPowerUp(enemigo);
					}
					if (b.isDestroyed())
					{
						break; // una bala normal solo golpea a un enemigo (la Railgun atraviesa)
					}
				}
			}

			if (b.isDestroyed())
			{
				balas.remove(b);
				i--; // para no saltarse 1 tras eliminar del arraylist
			}
		}

		// actualizar movimiento de enemigos dentro del área
		for (Enemigo enemigo : enemigos)
		{
			enemigo.actualizar();
		}

		// colisiones entre enemigos y sus rebotes (cada pareja se revisa una sola vez)
		for (int i = 0; i < enemigos.size(); i++)
		{
			for (int j = i + 1; j < enemigos.size(); j++)
			{
				enemigos.get(i).revisarColisionEnemigo(enemigos.get(j));
			}
		}

		// colisión de los enemigos con la nave
		for (int i = 0; i < enemigos.size(); i++)
		{
			// perdió vida o game over
			if (nave.revisarColision(enemigos.get(i)))
			{
				locutor.registrarDanio();
				// el enemigo se destruye con el choque
				enemigos.remove(i);
				i--;
			}
		}

		// power-ups: caen, y si la nave los toca se aplica su efecto
		for (int i = 0; i < powerUps.size(); i++)
		{
			PowerUp p = powerUps.get(i);
			p.actualizar();
			if (nave.chocaCon(p))
			{
				p.aplicar(nave);
				locutor.anunciar(p.getMensaje());
				powerUps.remove(i);
				i--;
			}
			else if (p.salioDePantalla())
			{
				powerUps.remove(i);
				i--;
			}
		}
	}

	// Con cierta probabilidad (la define el Nivel) el enemigo muerto suelta un power-up al azar
	private void soltarPowerUp(Enemigo muerto)
	{
		if (MathUtils.random() < nivel.getProbPowerUp())
		{
			float x = muerto.getX() + muerto.getAncho() / 2 - 24;
			float y = muerto.getY();
			switch (MathUtils.random(2))
			{
				case 0:
					powerUps.add(new Pizza(x, y));
					break;
				case 1:
					powerUps.add(new SillaGamer(x, y));
					break;
				default:
					powerUps.add(new RGB(x, y));
					break;
			}
		}
	}

	// ---------- PASO 2 del template: dibujar ----------
	@Override
	protected void dibujar()
	{
		hud.dibujar(game.getBatch(), game.getFont(), nave, nivel, score, game.getHighScore());

		for (Bullet b : balas)
		{
			b.dibujar(game.getBatch());
		}

		for (PowerUp p : powerUps)
		{
			p.dibujar(game.getBatch());
		}

		nave.dibujar(game.getBatch());

		for (Enemigo enemigo : enemigos)
		{
			enemigo.dibujar(game.getBatch());
		}

		locutor.dibujar(game.getBatch(), game.getFont());

		if (pausado)
		{
			dibujarPausa();
		}
	}

	// Oscurece la pantalla y muestra el cartel de pausa
	private void dibujarPausa()
	{
		game.getBatch().setColor(0, 0, 0, 0.6f);
		game.getBatch().draw(GestorRecursos.getInstance().getPixelBlanco(), 0, 0, Constantes.ANCHO, Constantes.ALTO);
		game.getBatch().setColor(Color.WHITE);
		game.getFont().getData().setScale(3f);
		game.getFont().draw(game.getBatch(), "PAUSA - AFK", 0, Constantes.ALTO / 2 + 30, Constantes.ANCHO, Align.center, false);
		game.getFont().getData().setScale(2f);
		game.getFont().draw(game.getBatch(), "Presiona P para volver", 0, Constantes.ALTO / 2 - 30, Constantes.ANCHO, Align.center, false);
	}

	// ---------- PASO 3 del template (gancho): ¿hay que cambiar de pantalla? ----------
	@Override
	protected void revisarTransicion()
	{
		if (nave.estaDestruido())
		{
			// Se guarda el récord (si corresponde) y se muestra la pantalla de fin
			boolean nuevoRecord = GestorRecursos.getInstance().registrarPuntaje(score);
			game.setScreen(new PantallaGameOver(game, score, nuevoRecord));
		}
		else if (enemigos.size() == 0)
		{
			// Oleada completada: el Director arma la siguiente y se sigue con la misma nave
			DirectorNiveles director = new DirectorNiveles(new NivelPartidaBuilder());
			game.setScreen(new PantallaJuego(game, director.construirOleada(nivel.getNumero() + 1), nave, score, locutor));
		}
	}

	@Override
	public void show()
	{
		gameMusic.play();
	}
}
