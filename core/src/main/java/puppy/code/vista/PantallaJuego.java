package puppy.code.vista;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import puppy.code.modelo.powerups.LootBox;
import puppy.code.modelo.powerups.Pizza;
import puppy.code.modelo.powerups.PowerUp;
import puppy.code.modelo.powerups.RGB;
import puppy.code.modelo.powerups.SillaGamer;
import puppy.code.util.GestorRecursos;

/*
    Pantalla donde se juega. Hereda de PantallaBase (Template Method), así que
    solo define los tres pasos: actualizar (lógica), dibujar y revisarTransicion.

    Cada oleada pasa por tres fases:
      1. INTRO:       cartel con el nombre de la oleada, la nave aparece y los enemigos
                      van entrando de a uno desde arriba.
      2. JUGANDO:     se pelea hasta eliminar a todos los enemigos.
      3. COMPLETADA:  aviso de oleada completada y después empieza la siguiente.
*/
public class PantallaJuego extends PantallaBase
{
	private enum Fase { INTRO, JUGANDO, COMPLETADA }

	// Tiempos en fotogramas (a 60 por segundo)
	private static final int DURACION_INTRO = 170;
	private static final int RETRASO_ENTRADA = 110;    // cuándo entra el primer enemigo
	private static final int INTERVALO_ENTRADA = 16;   // tiempo entre un enemigo y el siguiente
	private static final int DURACION_COMPLETADA = 130;

	private Sound explosionSound;
	private Music gameMusic;
	private int score;
	private Nivel nivel;

	private Nave4 nave;
	private Locutor locutor;
	private Hud hud = new Hud();
	private boolean pausado = false;

	private Fase fase = Fase.INTRO;
	private int tiempoFase = 0;     // fotogramas que lleva la fase actual
	private int tiempoOleada = 0;   // fotogramas desde que empezó la oleada
	private Random azar = new Random();

	private ArrayList<Enemigo> pendientes = new ArrayList<>(); // enemigos que todavía no han entrado
	private ArrayList<Enemigo> enemigos = new ArrayList<>();   // enemigos que ya están en pantalla
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

		nave.aparecer();
		crearEnemigos(recursos);
	}

	// Prepara los enemigos que pide el Nivel. No aparecen todos juntos: esperan en la cola "pendientes"
	private void crearEnemigos(GestorRecursos recursos)
	{
		for (int i = 0; i < nivel.getCantBots(); i++)
		{
			pendientes.add(new EnemigoBot(0, 0, velocidad(), velocidad(), recursos.getTextura("bot.png")));
		}
		for (int i = 0; i < nivel.getCantLags(); i++)
		{
			pendientes.add(new EnemigoLag(0, 0, velocidad() * 1.6f, velocidad() * 1.6f,
					recursos.getTextura("lag.png")));
		}
		for (int i = 0; i < nivel.getCantCheaters(); i++)
		{
			pendientes.add(new EnemigoCheater(0, 0, velocidad() * 0.5f, velocidad() * 0.5f,
					recursos.getTextura("cheater.png")));
		}
		// Se mezclan para que no salgan todos los bots primero y después los demás
		java.util.Collections.shuffle(pendientes, azar);
	}

	// Velocidad al azar (entre 1,5 y 3, escalada por la dificultad del nivel) y con dirección al azar
	private float velocidad()
	{
		float v = (1.5f + azar.nextFloat() * 1.5f) * nivel.getVelocidad();
		return azar.nextBoolean() ? v : -v;
	}

	// Saca al siguiente enemigo de la cola y lo hace entrar por arriba
	private void hacerEntrarSiguiente()
	{
		Enemigo e = pendientes.remove(0);
		e.iniciarEntrada(60 + azar.nextInt(Constantes.ANCHO - 200));
		enemigos.add(e);
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

		tiempoFase++;
		tiempoOleada++;
		locutor.actualizar();
		nave.actualizar();

		// Cambios de fase
		if (fase == Fase.INTRO && tiempoFase >= DURACION_INTRO)
		{
			fase = Fase.JUGANDO;
			tiempoFase = 0;
		}

		// Entrada escalonada de los enemigos
		if (fase != Fase.COMPLETADA && !pendientes.isEmpty() && tiempoOleada >= RETRASO_ENTRADA
				&& (tiempoOleada - RETRASO_ENTRADA) % INTERVALO_ENTRADA == 0)
		{
			hacerEntrarSiguiente();
		}

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
				if (enemigo.isEntrando())
				{
					continue; // todavía está entrando: no se le puede disparar
				}
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
				if (!enemigos.get(i).isEntrando() && !enemigos.get(j).isEntrando())
				{
					enemigos.get(i).revisarColisionEnemigo(enemigos.get(j));
				}
			}
		}

		// colisión de los enemigos con la nave
		for (int i = 0; i < enemigos.size(); i++)
		{
			if (enemigos.get(i).isEntrando())
			{
				continue;
			}
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

		// ¿Se eliminó a todos? Entonces la oleada está completada
		if (fase == Fase.JUGANDO && pendientes.isEmpty() && enemigos.isEmpty())
		{
			fase = Fase.COMPLETADA;
			tiempoFase = 0;
			int bono = 50 * nivel.getNumero();
			score += bono;
			locutor.anunciar("¡OLEADA COMPLETADA! +" + bono);
		}
	}

	// Con cierta probabilidad (la define el Nivel) el enemigo muerto suelta un power-up al azar
	private void soltarPowerUp(Enemigo muerto)
	{
		if (MathUtils.random() < nivel.getProbPowerUp())
		{
			float x = muerto.getX() + muerto.getAncho() / 2 - 24;
			float y = muerto.getY();
			switch (MathUtils.random(3))
			{
				case 0:
					powerUps.add(new Pizza(x, y));
					break;
				case 1:
					powerUps.add(new SillaGamer(x, y));
					break;
				case 2:
					powerUps.add(new LootBox(x, y));
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

		if (fase == Fase.INTRO)
		{
			dibujarIntro();
		}

		if (pausado)
		{
			dibujarPausa();
		}
	}

	// Cartel del comienzo de la oleada (se desvanece al final de la intro)
	private void dibujarIntro()
	{
		BitmapFont font = game.getFont();
		float transparencia = Math.min(1f, (DURACION_INTRO - tiempoFase) / 30f);

		font.getData().setScale(5f);
		font.setColor(1f, 0.85f, 0.2f, transparencia);
		font.draw(game.getBatch(), "OLEADA " + nivel.getNumero(), 0, Constantes.ALTO / 2 + 140,
				Constantes.ANCHO, Align.center, false);

		font.getData().setScale(2.5f);
		font.setColor(0.4f, 0.9f, 1f, transparencia);
		font.draw(game.getBatch(), nivel.getNombre(), 0, Constantes.ALTO / 2 + 50, Constantes.ANCHO, Align.center, false);

		// Antes de que entre el primer enemigo: "preparate"; después: "¡a pelear!"
		font.getData().setScale(2.5f);
		font.setColor(1f, 1f, 1f, transparencia);
		String mensaje = tiempoOleada < RETRASO_ENTRADA ? "¡PREPÁRATE!" : "¡ELIMINA A TODOS!";
		font.draw(game.getBatch(), mensaje, 0, Constantes.ALTO / 2 - 30, Constantes.ANCHO, Align.center, false);

		font.setColor(Color.WHITE);
		font.getData().setScale(2f);
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
		else if (fase == Fase.COMPLETADA && tiempoFase >= DURACION_COMPLETADA)
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
