package puppy.code.vista;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.Bullet;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.Dificultad;
import puppy.code.modelo.Nave4;
import puppy.code.modelo.Personaje;
import puppy.code.modelo.armas.BFG;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.enemigos.EnemigoBot;
import puppy.code.modelo.enemigos.EnemigoCheater;
import puppy.code.modelo.enemigos.EnemigoJefe;
import puppy.code.modelo.enemigos.EnemigoLag;
import puppy.code.modelo.enemigos.EnemigoSniper;
import puppy.code.modelo.enemigos.EnemigoSpammer;
import puppy.code.modelo.enemigos.TipoJefe;
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
	private static final int DURACION_INTRO = 330;
	private static final int RETRASO_ENTRADA = 200;    // cuándo entra el primer enemigo
	private static final int INTERVALO_ENTRADA = 24;   // tiempo entre un enemigo y el siguiente dentro de un grupo
	private static final String PISTA_JUEGO = "piano-loops.wav";
	private static final String PISTA_JEFE = "musica_jefe.wav";
	private static final int ENFRIAMIENTO_POWERUP = 600; // mínimo de fotogramas entre un power-up y el siguiente
	private static final int ESPERA_MAXIMA_GRUPO = 420;  // si pasa este tiempo sin entrar nadie, entra el siguiente grupo igual
	private static final int QUEDAN_PARA_NUEVO_GRUPO = 2; // cuando quedan tan pocos enemigos, entra el siguiente grupo
	private static final int DURACION_COMPLETADA = 210;
	private static final int MAX_ENEMIGOS = 14;       // tope para que el jefe no llene la pantalla de Lags

	private String pistaDeseada = PISTA_JUEGO;   // la canción que debería sonar ahora
	private int score;
	private Nivel nivel;

	private Nave4 nave;
	private Locutor locutor;
	private Hud hud = new Hud();
	private BarraJefe barraJefe = new BarraJefe();
	private boolean pausado = false;
	private Dificultad dificultad;
	private int accionPausa = -1;   // botón del menú de pausa que se accionó
	private MenuBotones menuPausa = new MenuBotones(new String[] { "CONTINUAR", "OPCIONES", "SALIR AL MENÚ" },
			Constantes.ANCHO / 2f - 190, 430, 380, 60, 18, false);

	private Fase fase = Fase.INTRO;
	private int tiempoFase = 0;     // fotogramas que lleva la fase actual
	private int tiempoOleada = 0;   // fotogramas desde que empezó la oleada
	private Random azar = new Random();

	private int entrantesDelGrupo = 0;    // cuántos enemigos faltan por entrar del grupo actual
	private int esperaGrupo = 0;          // fotogramas desde que entró el último enemigo
	private float xBaseGrupo = 0;         // por dónde entra el grupo actual
	private int indiceEnGrupo = 0;
	private int enfriamientoPowerUp = 0;  // cuando es mayor que 0 no caen power-ups
	private ArrayList<Enemigo> pendientes = new ArrayList<>(); // enemigos que todavía no han entrado
	private ArrayList<Enemigo> enemigos = new ArrayList<>();   // enemigos que ya están en pantalla
	private ArrayList<Bullet> balas;   // las balas del jugador (están en el contexto del combate)
	private ContextoCombate contexto;
	private ArrayList<BalaEnemiga> balasEnemigas;
	private ArrayList<PowerUp> powerUps = new ArrayList<>();

	// Arma una partida nueva: oleada 1, la nave del personaje elegido y puntaje 0
	public static PantallaJuego nuevaPartida(SpaceNavigation game, Personaje personaje)
	{
		Nave4 nave = new Nave4(personaje);
		// la dificultad elegida en las opciones puede dar vidas extra
		nave.setVidas(nave.getVidas() + GestorRecursos.getInstance().getOpciones().getDificultad().getVidasExtra());
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
		dificultad = recursos.getOpciones().getDificultad();
		contexto = new ContextoCombate(nave, dificultad, nivel.getNumero());
		balasEnemigas = contexto.getBalasEnemigas();
		balas = contexto.getBalasJugador();
		recursos.reproducirMusica(pistaDeseada);

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
			pendientes.add(new EnemigoLag(0, 0, velocidad() * 1.3f, velocidad() * 1.3f,
					recursos.getTextura("lag.png")));
		}
		for (int i = 0; i < nivel.getCantCheaters(); i++)
		{
			pendientes.add(new EnemigoCheater(0, 0, velocidad() * 0.5f, velocidad() * 0.5f,
					recursos.getTextura("cheater.png")));
		}
		for (int i = 0; i < nivel.getCantSpammers(); i++)
		{
			pendientes.add(new EnemigoSpammer(0, 0, Math.abs(velocidad()) * 1.1f * (azar.nextBoolean() ? 1 : -1), 0,
					recursos.getTextura("spammer.png")));
		}
		for (int i = 0; i < nivel.getCantSnipers(); i++)
		{
			pendientes.add(new EnemigoSniper(0, 0, velocidad() * 0.5f, velocidad() * 0.5f, recursos.getTextura("sniper.png")));
		}
		// Se mezclan para que no salgan todos los bots primero y después los demás
		java.util.Collections.shuffle(pendientes, azar);

		// Si la oleada trae un jefe, es el primero en entrar
		if (nivel.tieneJefe())
		{
			TipoJefe tipo = nivel.getTipoJefe();
			pendientes.add(0, new EnemigoJefe(tipo, recursos.getTextura(tipo.getArchivoImagen())));
		}
	}

	// Velocidad al azar (entre 1,5 y 3, escalada por la dificultad del nivel) y con dirección al azar
	private float velocidad()
	{
		float v = Math.min((1.2f + azar.nextFloat() * 1.2f) * nivel.getVelocidad() * dificultad.getMultVelocidad(), 4.5f);
		return azar.nextBoolean() ? v : -v;
	}

	// Saca al siguiente enemigo de la cola y lo hace entrar por arriba, en su lugar dentro del grupo
	private void hacerEntrarSiguiente()
	{
		Enemigo e = pendientes.remove(0);
		float x = xBaseGrupo + indiceEnGrupo * 230 + azar.nextInt(40);
		e.iniciarEntrada(Math.min(x, Constantes.ANCHO - 120));
		indiceEnGrupo++;
		enemigos.add(e);
	}

	// Con un jefe en pantalla suena la canción tensa; cuando no hay jefe, la del juego
	private void actualizarMusica()
	{
		boolean hayJefe = false;
		for (Enemigo e : enemigos)
		{
			if (e instanceof EnemigoJefe)
			{
				hayJefe = true;
			}
		}
		String pista = hayJefe ? PISTA_JEFE : PISTA_JUEGO;
		if (!pista.equals(pistaDeseada))
		{
			pistaDeseada = pista;
			GestorRecursos.getInstance().reproducirMusica(pista);
		}
	}

	// Cuántos enemigos normales hay en pantalla (el jefe no cuenta)
	private int contarNormales()
	{
		int n = 0;
		for (Enemigo e : enemigos)
		{
			if (!(e instanceof EnemigoJefe))
			{
				n++;
			}
		}
		return n;
	}

	/*
		Los enemigos entran en GRUPOS PEQUEÑOS (mini-oleadas) y no todos de una vez:
		cuando el grupo anterior ya casi no tiene enemigos (o pasó mucho tiempo) entra el
		siguiente, y dentro del grupo entran de a uno, separados.
	*/
	private void actualizarEntradaDeEnemigos()
	{
		if (fase == Fase.COMPLETADA || pendientes.isEmpty() || tiempoOleada < RETRASO_ENTRADA)
		{
			return;
		}
		esperaGrupo++;

		if (entrantesDelGrupo > 0)
		{
			if (esperaGrupo >= INTERVALO_ENTRADA)
			{
				hacerEntrarSiguiente();
				entrantesDelGrupo--;
				esperaGrupo = 0;
			}
		}
		else if (contarNormales() <= QUEDAN_PARA_NUEVO_GRUPO || esperaGrupo >= ESPERA_MAXIMA_GRUPO)
		{
			int tamano = Math.min(2 + nivel.getNumero() / 5, 4);
			entrantesDelGrupo = Math.min(tamano, pendientes.size());
			indiceEnGrupo = 0;
			xBaseGrupo = 60 + azar.nextInt(Math.max(1, Constantes.ANCHO - 200 - 230 * (entrantesDelGrupo - 1)));
			esperaGrupo = INTERVALO_ENTRADA; // el primero entra enseguida
		}
	}

	// ---------- PASO 1 del template: actualizar la lógica ----------
	@Override
	protected void actualizar(float delta)
	{
		// P o ESC pausan y reanudan el juego
		if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
		{
			pausado = !pausado;
		}
		if (pausado)
		{
			accionPausa = menuPausa.actualizar(viewport);
			if (accionPausa == 0)
			{
				pausado = false; // CONTINUAR
				accionPausa = -1;
			}
			return;
		}

		tiempoFase++;
		tiempoOleada++;

		// ENTER salta el cartel de inicio y el aviso de oleada completada
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && tiempoFase > 40)
		{
			if (fase == Fase.INTRO)
			{
				tiempoFase = DURACION_INTRO;
				tiempoOleada = Math.max(tiempoOleada, RETRASO_ENTRADA);
			}
			else if (fase == Fase.COMPLETADA)
			{
				tiempoFase = DURACION_COMPLETADA;
			}
		}
		locutor.actualizar();
		nave.actualizar();

		// Cambios de fase
		if (fase == Fase.INTRO && tiempoFase >= DURACION_INTRO)
		{
			fase = Fase.JUGANDO;
			tiempoFase = 0;
		}

		actualizarMusica();
		actualizarEntradaDeEnemigos();

		if (enfriamientoPowerUp > 0)
		{
			enfriamientoPowerUp--;
		}

		// X: usa la habilidad especial si la barra está llena
		if (Gdx.input.isKeyJustPressed(Input.Keys.X) && nave.usarHabilidad(contexto))
		{
			locutor.anunciar("¡" + nave.getHabilidad().getNombre().toUpperCase() + "!");
			GestorRecursos.getInstance().reproducirSonido("habilidad.wav");
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
						GestorRecursos.getInstance().reproducirSonido("explosion.ogg");
						score += enemigo.getPuntos();
						nave.sumarEnergia(enemigo.getPuntos() * 0.8f); // cada baja carga la habilidad
						locutor.registrarEliminacion();
						if (enemigo instanceof EnemigoJefe)
						{
							locutor.anunciar("¡JEFE ELIMINADO! GG");
						}
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

		// los enemigos atacan (disparan, invocan...) solo cuando la oleada ya está en juego
		if (fase == Fase.JUGANDO && !nave.estaApareciendo())
		{
			for (Enemigo enemigo : enemigos)
			{
				if (!enemigo.isEntrando())
				{
					enemigo.atacar(contexto);
				}
			}
		}
		java.util.List<Enemigo> nuevos = contexto.recogerNuevos();
		if (enemigos.size() < MAX_ENEMIGOS)
		{
			enemigos.addAll(nuevos);
		}

		// balas enemigas: se mueven y, si tocan la nave, le hacen daño
		for (int i = 0; i < balasEnemigas.size(); i++)
		{
			BalaEnemiga bala = balasEnemigas.get(i);
			bala.actualizar();
			int vidasAntes = nave.getVidas();
			if (nave.revisarGolpe(bala))
			{
				if (nave.getVidas() < vidasAntes)
				{
					locutor.registrarDanio();
				}
				bala.destruir();
			}
			if (bala.isDestruida() || bala.salioDePantalla())
			{
				balasEnemigas.remove(i);
				i--;
			}
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
				// el enemigo se destruye con el choque (el jefe no)
				if (enemigos.get(i).seDestruyeAlChocar())
				{
					enemigos.remove(i);
					i--;
				}
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
				GestorRecursos.getInstance().reproducirSonido("powerup.wav");
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
			balasEnemigas.clear(); // al terminar la oleada desaparecen las balas enemigas
			int bono = 50 * nivel.getNumero();
			score += bono;
			locutor.anunciar("¡OLEADA COMPLETADA! +" + bono);
		}
	}

	// Con cierta probabilidad (la define el Nivel) el enemigo muerto suelta un power-up al azar
	private void soltarPowerUp(Enemigo muerto)
	{
		boolean seguro = muerto.sueltaPowerUpSeguro();
		if (seguro || (enfriamientoPowerUp == 0 && MathUtils.random() < nivel.getProbPowerUp()))
		{
			enfriamientoPowerUp = ENFRIAMIENTO_POWERUP;
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
		hud.dibujar(game.getBatch(), game.getFont(), nave, nivel, score, game.getHighScore(),
				pendientes.size() + enemigos.size());

		for (Bullet b : balas)
		{
			b.dibujar(game.getBatch());
		}

		for (PowerUp p : powerUps)
		{
			p.dibujar(game.getBatch());
		}

		nave.dibujar(game.getBatch());

		for (BalaEnemiga be : balasEnemigas)
		{
			be.dibujar(game.getBatch());
		}

		for (Enemigo enemigo : enemigos)
		{
			enemigo.dibujar(game.getBatch());
			if (enemigo instanceof EnemigoJefe && !enemigo.isEntrando())
			{
				barraJefe.dibujar(game.getBatch(), game.getFont(), (EnemigoJefe) enemigo);
			}
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

		font.getData().setScale(3.5f);
		font.setColor(1f, 0.85f, 0.2f, transparencia);
		font.draw(game.getBatch(), "OLEADA " + nivel.getNumero() + "/" + DirectorNiveles.TOTAL_OLEADAS, 0, Constantes.ALTO / 2 + 140,
				Constantes.ANCHO, Align.center, false);

		font.getData().setScale(2f);
		font.setColor(0.4f, 0.9f, 1f, transparencia);
		font.draw(game.getBatch(), nivel.getNombre(), 0, Constantes.ALTO / 2 + 50, Constantes.ANCHO, Align.center, false);

		// Aviso de la oleada (enemigo nuevo, jefe, etc.)
		font.getData().setScale(1.5f);
		font.setColor(1f, 0.6f, 0.2f, transparencia);
		font.draw(game.getBatch(), nivel.getAviso(), 0, Constantes.ALTO / 2 - 10, Constantes.ANCHO, Align.center, false);

		// Antes de que entre el primer enemigo: "preparate"; después: "¡a pelear!"
		font.getData().setScale(2f);
		font.setColor(1f, 1f, 1f, transparencia);
		String mensaje = tiempoOleada < RETRASO_ENTRADA ? "¡PREPÁRATE!" : "¡ELIMINA A TODOS!";
		font.draw(game.getBatch(), mensaje, 0, Constantes.ALTO / 2 - 90, Constantes.ANCHO, Align.center, false);

		font.getData().setScale(1f);
		font.setColor(0.7f, 0.7f, 0.8f, transparencia);
		font.draw(game.getBatch(), "ENTER: saltar", 0, Constantes.ALTO / 2 - 160, Constantes.ANCHO, Align.center, false);

		font.setColor(Color.WHITE);
		font.getData().setScale(1.5f);
	}

	// Oscurece la pantalla y muestra el cartel de pausa
	private void dibujarPausa()
	{
		game.getBatch().setColor(0, 0, 0, 0.7f);
		game.getBatch().draw(GestorRecursos.getInstance().getPixelBlanco(), 0, 0, Constantes.ANCHO, Constantes.ALTO);
		game.getBatch().setColor(Color.WHITE);
		game.getFont().getData().setScale(3f);
		game.getFont().draw(game.getBatch(), "PAUSA - AFK", 0, Constantes.ALTO - 160, Constantes.ANCHO, Align.center, false);
		menuPausa.dibujar(game.getBatch(), game.getFont());
		game.getFont().getData().setScale(1f);
		game.getFont().draw(game.getBatch(), "P o ESC: continuar", 0, 130, Constantes.ANCHO, Align.center, false);
		game.getFont().getData().setScale(1.5f);
	}

	// ---------- PASO 3 del template (gancho): ¿hay que cambiar de pantalla? ----------
	@Override
	protected void revisarTransicion()
	{
		if (accionPausa == 1)
		{
			accionPausa = -1;
			game.setScreen(new PantallaOpciones(game, this));
			return;
		}
		if (accionPausa == 2)
		{
			accionPausa = -1;
			game.setScreen(new PantallaMenu(game));
			return;
		}

		if (nave.estaDestruido())
		{
			// Se guarda el récord (si corresponde) y se muestra la pantalla de fin
			boolean nuevoRecord = GestorRecursos.getInstance().registrarPuntaje(score);
			game.setScreen(new PantallaGameOver(game, score, nuevoRecord));
		}
		else if (fase == Fase.COMPLETADA && tiempoFase >= DURACION_COMPLETADA
				&& nivel.getNumero() >= DirectorNiveles.TOTAL_OLEADAS)
		{
			// Se completó la última oleada: se guarda el récord y se muestra la victoria
			boolean nuevoRecord = GestorRecursos.getInstance().registrarPuntaje(score);
			game.setScreen(new PantallaVictoria(game, score, nuevoRecord));
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
		GestorRecursos.getInstance().reproducirMusica(pistaDeseada);
	}
}
