package puppy.code.vista;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.modelo.Bullet;
import puppy.code.modelo.Nave4;
import puppy.code.modelo.armas.Escopeta;
import puppy.code.modelo.armas.Pistola;
import puppy.code.modelo.armas.Railgun;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.enemigos.EnemigoBot;
import puppy.code.modelo.enemigos.EnemigoCheater;
import puppy.code.modelo.enemigos.EnemigoLag;
import puppy.code.modelo.niveles.DirectorNiveles;
import puppy.code.modelo.niveles.Nivel;
import puppy.code.modelo.niveles.NivelPartidaBuilder;
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
	// Una sola lista para todos los enemigos
	private ArrayList<Enemigo> enemigos = new ArrayList<>();
	private ArrayList<Bullet> balas = new ArrayList<>();

	// Arma una partida nueva: oleada 1, una nave nueva y puntaje 0
	public static PantallaJuego nuevaPartida(SpaceNavigation game)
	{
		GestorRecursos recursos = GestorRecursos.getInstance();
		Nave4 nave = new Nave4(Constantes.ANCHO / 2 - 32, 30, recursos.getTextura("MainShip3.png"),
				recursos.getSonido("hurt.ogg"));

		// El Director usa el Builder para construir la primera oleada
		DirectorNiveles director = new DirectorNiveles(new NivelPartidaBuilder());
		return new PantallaJuego(game, director.construirOleada(1), nave, 0);
	}

	public PantallaJuego(SpaceNavigation game, Nivel nivel, Nave4 nave, int score)
	{
		super(game);
		this.nivel = nivel;
		this.nave = nave;
		this.score = score;

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
		nave.actualizar();

		// Teclas provisorias para probar las armas (Strategy): 1 = Pistola, 2 = Escopeta, 3 = Railgun
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) nave.setEstrategia(new Pistola());
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) nave.setEstrategia(new Escopeta());
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) nave.setEstrategia(new Railgun());

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
						enemigos.remove(j);
						j--;
						enemigos.addAll(enemigo.alMorir()); // por ejemplo, el Cheater suelta 2 Lags
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
				// el enemigo se destruye con el choque
				enemigos.remove(i);
				i--;
			}
		}
	}

	// ---------- PASO 2 del template: dibujar ----------
	@Override
	protected void dibujar()
	{
		dibujaEncabezado();

		for (Bullet b : balas)
		{
			b.dibujar(game.getBatch());
		}

		nave.dibujar(game.getBatch());

		for (Enemigo enemigo : enemigos)
		{
			enemigo.dibujar(game.getBatch());
		}
	}

	public void dibujaEncabezado()
	{
		CharSequence str = "Vidas: " + nave.getVidas() + " Oleada: " + nivel.getNumero() + "  Arma: "
				+ nave.getNombreArma();
		game.getFont().getData().setScale(2f);
		game.getFont().draw(game.getBatch(), str, 10, 30);
		game.getFont().draw(game.getBatch(), "Score:" + this.score, Constantes.ANCHO - 150, 30);
		game.getFont().draw(game.getBatch(), "Récord:" + game.getHighScore(), Constantes.ANCHO / 2 - 60, 30);
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
			game.setScreen(new PantallaJuego(game, director.construirOleada(nivel.getNumero() + 1), nave, score));
		}
	}

	@Override
	public void show()
	{
		gameMusic.play();
	}
}
