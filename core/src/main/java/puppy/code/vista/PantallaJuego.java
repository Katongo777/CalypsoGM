package puppy.code.vista;

import java.util.ArrayList;
import java.util.List;
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
import puppy.code.modelo.EfectosCombate;
import puppy.code.modelo.Nave4;
import puppy.code.modelo.Personaje;
import puppy.code.modelo.Rayo;
import puppy.code.modelo.armas.BFG;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.niveles.DirectorNiveles;
import puppy.code.modelo.niveles.Nivel;
import puppy.code.modelo.niveles.NivelPartidaBuilder;
import puppy.code.modelo.powerups.GeneradorPowerUps;
import puppy.code.modelo.powerups.PowerUp;
import puppy.code.util.GestorRecursos;

/*
    Pantalla donde se juega. Hereda de PantallaBase (Template Method), así que
    solo define los tres pasos: actualizar (lógica), dibujar y revisarTransicion.

    También implementa EfectosCombate: cuando un enemigo aparece o muere, la pantalla se lo
    avisa y el enemigo pide los efectos que quiera (anuncio, temblor, música...). Así la
    pantalla no necesita saber de qué tipo es cada enemigo.

    Cada oleada pasa por tres fases:
      1. INTRO:       cartel con el nombre de la oleada, la nave aparece y los enemigos
                      van entrando de a uno desde arriba.
      2. JUGANDO:     se pelea hasta eliminar a todos los enemigos.
      3. COMPLETADA:  aviso de oleada completada y después empieza la siguiente.
*/
public class PantallaJuego extends PantallaBase implements EfectosCombate
{
	private enum Fase { INTRO, JUGANDO, COMPLETADA }

	// Tiempos en fotogramas (a 60 por segundo)
	private static final int DURACION_INTRO = 330;
	private static final int RETRASO_ENTRADA = 200;    // cuándo entra el primer enemigo
	private static final int INTERVALO_ENTRADA = 24;   // tiempo entre un enemigo y el siguiente dentro de un grupo
	private static final String PISTA_JUEGO = "piano-loops.wav";
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
	private final List<Enemigo> pendientes = new ArrayList<>(); // enemigos que todavía no han entrado
	private final List<Enemigo> enemigos = new ArrayList<>();   // enemigos que ya están en pantalla
	private final List<Bullet> balas = new ArrayList<>();       // las balas del jugador
	private ContextoCombate contexto;
	private final List<BalaEnemiga> balasEnemigas = new ArrayList<>();
	private final List<Rayo> rayos = new ArrayList<>();
	private Particulas particulas = new Particulas();
	private int vidasPrevias;            // para notar cuándo se pierde una vida
	private int temblorFrames = 0;       // efectos de impacto: temblor, destello y congelado
	private int temblorTotal = 1;
	private float temblorAmplitud = 0;
	private int destelloFrames = 0;
	private int destelloTotal = 1;
	private Color destelloColor = Color.WHITE;
	private int congelarFrames = 0;      // "hit-stop": el juego se detiene un instante para dar peso al golpe
	private final List<PowerUp> powerUps = new ArrayList<>();
	private final GeneradorPowerUps generadorPowerUps = new GeneradorPowerUps();

	// Arma una partida nueva: oleada 1, la nave del personaje elegido y puntaje 0
	public static PantallaJuego nuevaPartida(SpaceNavigation game, Personaje personaje)
	{
		Nave4 nave = new Nave4(personaje);
		// la dificultad elegida en las opciones puede dar vidas extra
		nave.curar(GestorRecursos.getInstance().getOpciones().getDificultad().getVidasExtra());
		if (game.isModoDios())
		{
			nave.asegurarVidas(30); // easter egg del código Konami: 30 vidas y el BFG 9000
			nave.setEstrategia(new BFG());
		}

		// El Director usa el Builder para construir la primera oleada (con la dificultad elegida)
		DirectorNiveles director = new DirectorNiveles(new NivelPartidaBuilder(GestorRecursos.getInstance().getOpciones().getDificultad()));
		// Para probar los jefes sin jugar todas las oleadas: java -Dcalypso.oleada=10 (se ignora si no se pone)
		int oleadaInicial = Math.max(1, Math.min(Integer.getInteger("calypso.oleada", 1), DirectorNiveles.TOTAL_OLEADAS));
		return new PantallaJuego(game, director.construirOleada(oleadaInicial), nave, 0, new Locutor());
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
		recursos.reproducirMusica(pistaDeseada);
		vidasPrevias = nave.getVidas();

		nave.aparecer();
		if (nivel.tieneJefe())
		{
			nave.asegurarVidas(3); // antes de cada jefe te dan hasta 3 vidas
		}
		prepararEnemigos();
	}

	/*
		Los enemigos ya vienen creados en el Nivel (los crea el Builder). No aparecen todos
		juntos: esperan en la cola "pendientes". Si la oleada trae jefe, es el primero en entrar.
	*/
	private void prepararEnemigos()
	{
		if (nivel.tieneJefe())
		{
			pendientes.add(nivel.getJefe());
		}
		pendientes.addAll(nivel.getEnemigos());
	}

	// Saca al siguiente enemigo de la cola y lo hace entrar por arriba, en su lugar dentro del grupo
	private void hacerEntrarSiguiente()
	{
		Enemigo e = pendientes.remove(0);
		float x = xBaseGrupo + indiceEnGrupo * 230 + azar.nextInt(40);
		e.iniciarEntrada(Math.min(x, Constantes.ANCHO - 120));
		indiceEnGrupo++;
		enemigos.add(e);
		e.alAparecer(this); // cada enemigo decide si se anuncia (el jefe sí)
	}

	// Cuántos enemigos en pantalla cuentan para el grupo (cada enemigo lo dice: el jefe no cuenta)
	private int contarDelGrupo()
	{
		int n = 0;
		for (Enemigo e : enemigos)
		{
			if (e.cuentaParaGrupo())
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
		else if (contarDelGrupo() <= QUEDAN_PARA_NUEVO_GRUPO || esperaGrupo >= ESPERA_MAXIMA_GRUPO)
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
			accionPausa = menuPausa.actualizar(getViewport());
			if (accionPausa == 0)
			{
				pausado = false; // CONTINUAR
				accionPausa = -1;
			}
			return;
		}

		// los efectos de impacto avanzan siempre; el congelado detiene el resto del juego unos fotogramas
		particulas.actualizar();
		if (temblorFrames > 0)
		{
			temblorFrames--;
		}
		if (destelloFrames > 0)
		{
			destelloFrames--;
		}
		if (congelarFrames > 0)
		{
			congelarFrames--;
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
			temblar(12, 6);
			destellar(new Color(1f, 0.9f, 0.4f, 1f), 14);
		}
		recogerDelContexto();

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
					particulas.explosion(b.getX() + b.getAncho() / 2, b.getY() + b.getAlto(), Color.WHITE, 3, 3f);
					// El enemigo pierde vida; solo muere cuando se le acaba
					enemigo.recibirDano(b.getDanio());
					if (enemigo.estaMuerto())
					{
						GestorRecursos.getInstance().reproducirSonido("explosion.ogg");
						score += enemigo.getPuntos();
						nave.sumarEnergia(enemigo.getPuntos() * 0.8f); // cada baja carga la habilidad
						locutor.registrarEliminacion();
						particulas.explosion(enemigo.getX() + enemigo.getAncho() / 2, enemigo.getY() + enemigo.getAlto() / 2,
								enemigo.getColorExplosion(), 14, 5f);
						enemigo.alSerDerrotado(this); // el jefe pide su gran explosión; los demás, nada más
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
		recogerDelContexto();
		List<Enemigo> nuevos = contexto.recogerNuevos();
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

		// el jefe puede pedir premios (por ejemplo, al romperse su escudo)
		for (float[] premio : contexto.recogerPremios())
		{
			agregarPowerUp(premio[0], premio[1], nave.getVidas() <= 2);
		}

		// los jefes pueden dejar mensajes y sonidos ("¡SE PONE UN ESCUDO!")
		for (String mensaje : contexto.recogerMensajes())
		{
			locutor.anunciar(mensaje);
		}
		for (String sonido : contexto.recogerSonidos())
		{
			GestorRecursos.getInstance().reproducirSonido(sonido);
		}

		// rayos: primero avisan y después dañan a la nave si la tocan
		for (int i = 0; i < rayos.size(); i++)
		{
			Rayo rayo = rayos.get(i);
			rayo.actualizar();
			int vidasAntes = nave.getVidas();
			if (rayo.estaActivo() && nave.revisarGolpe(rayo) && nave.getVidas() < vidasAntes)
			{
				locutor.registrarDanio();
			}
			if (rayo.estaTerminado())
			{
				rayos.remove(i);
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
			// perdió vida o getGame() over
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

		// ¿Perdió una vida? Entonces tiembla la pantalla, destella en rojo y el juego se congela un instante
		if (nave.getVidas() < vidasPrevias)
		{
			temblar(18, 10);
			destellar(new Color(1f, 0.15f, 0.15f, 1f), 22);
			congelarFrames = 6;
			particulas.explosion(nave.getX() + nave.getAncho() / 2, nave.getY() + nave.getAlto() / 2, new Color(1f, 0.4f, 0.4f, 1f), 20, 6f);
		}
		vidasPrevias = nave.getVidas();

		// ¿Se eliminó a todos? Entonces la oleada está completada
		if (fase == Fase.JUGANDO && pendientes.isEmpty() && enemigos.isEmpty())
		{
			fase = Fase.COMPLETADA;
			tiempoFase = 0;
			balasEnemigas.clear(); // al terminar la oleada desaparecen las balas enemigas
			rayos.clear();
			int bono = 50 * nivel.getNumero();
			score += bono;
			locutor.anunciar("¡OLEADA COMPLETADA! +" + bono);
		}
	}

	// Recoge lo que dejaron en el contexto los enemigos y las habilidades (balas, rayos, pedidos de limpieza)
	private void recogerDelContexto()
	{
		if (contexto.recogerPedidoLimpieza())
		{
			balasEnemigas.clear();
		}
		balasEnemigas.addAll(contexto.recogerBalasEnemigas());
		rayos.addAll(contexto.recogerRayos());
		balas.addAll(contexto.recogerBalasJugador());
	}

	// ---------- Efectos que piden los enemigos (interfaz EfectosCombate) ----------
	// Hace temblar la pantalla "frames" fotogramas con una intensidad máxima en píxeles
	@Override
	public void temblar(int frames, float amplitud)
	{
		temblorFrames = frames;
		temblorTotal = frames;
		temblorAmplitud = amplitud;
	}

	// Pinta toda la pantalla de un color que se va desvaneciendo
	@Override
	public void destellar(Color color, int frames)
	{
		destelloColor = color;
		destelloFrames = frames;
		destelloTotal = frames;
	}

	@Override
	public void anunciar(String texto)
	{
		locutor.anunciar(texto);
	}

	@Override
	public void reproducirSonido(String archivo)
	{
		GestorRecursos.getInstance().reproducirSonido(archivo);
	}

	@Override
	public void cambiarMusica(String archivo)
	{
		if (!archivo.equals(pistaDeseada))
		{
			pistaDeseada = archivo;
			GestorRecursos.getInstance().reproducirMusica(archivo);
		}
	}

	@Override
	public void congelar(int fotogramas)
	{
		congelarFrames = Math.max(congelarFrames, fotogramas);
	}

	@Override
	public void explotar(float x, float y, Color color, int cantidad, float fuerza)
	{
		particulas.explosion(x, y, color, cantidad, fuerza);
	}

	@Override
	public void limpiarAtaquesEnemigos()
	{
		balasEnemigas.clear();
		rayos.clear();
	}

	@Override
	public void curarNave(int vidas)
	{
		nave.curar(vidas);
	}

	// Gancho de PantallaBase: mientras hay temblor, la intensidad baja con el tiempo
	@Override
	protected float getTemblor()
	{
		return temblorFrames > 0 ? temblorAmplitud * temblorFrames / temblorTotal : 0;
	}

	/*
		Con cierta probabilidad (la define el Nivel) el enemigo muerto suelta un power-up al azar.
		Si al jugador le queda 1 sola vida la probabilidad sube al triple y el power-up es una pizza (+1 vida).
	*/
	private void soltarPowerUp(Enemigo muerto)
	{
		boolean seguro = muerto.sueltaPowerUpSeguro();
		boolean apuros = nave.getVidas() <= 1;
		float probabilidad = nivel.getProbPowerUp() * (apuros ? 3 : 1);
		if (seguro || (enfriamientoPowerUp == 0 && MathUtils.random() < probabilidad))
		{
			enfriamientoPowerUp = ENFRIAMIENTO_POWERUP;
			agregarPowerUp(muerto.getX() + muerto.getAncho() / 2 - 24, muerto.getY(), apuros);
		}
	}

	// Crea un power-up al azar en esa posición (si "curacion" es true, siempre es uno que cura)
	private void agregarPowerUp(float x, float y, boolean curacion)
	{
		powerUps.add(curacion ? generadorPowerUps.crearCuracion(x, y) : generadorPowerUps.crearAlAzar(x, y));
	}

	// ---------- PASO 2 del template: dibujar ----------
	@Override
	protected void dibujar()
	{
		hud.dibujar(getGame().getBatch(), getGame().getFont(), nave, nivel, score, getGame().getHighScore(),
				pendientes.size() + enemigos.size());

		for (Bullet b : balas)
		{
			b.dibujar(getGame().getBatch());
		}

		for (PowerUp p : powerUps)
		{
			p.dibujar(getGame().getBatch());
		}

		nave.dibujar(getGame().getBatch());

		for (Rayo r : rayos)
		{
			r.dibujar(getGame().getBatch());
		}

		for (BalaEnemiga be : balasEnemigas)
		{
			be.dibujar(getGame().getBatch());
		}

		for (Enemigo enemigo : enemigos)
		{
			enemigo.dibujar(getGame().getBatch());
			enemigo.dibujarInterfaz(getGame().getBatch(), getGame().getFont()); // por ejemplo, la barra del jefe
		}

		particulas.dibujar(getGame().getBatch());

		if (destelloFrames > 0)
		{
			getGame().getBatch().setColor(destelloColor.r, destelloColor.g, destelloColor.b, 0.4f * destelloFrames / destelloTotal);
			getGame().getBatch().draw(GestorRecursos.getInstance().getPixelBlanco(), 0, 0, Constantes.ANCHO, Constantes.ALTO);
			getGame().getBatch().setColor(Color.WHITE);
		}

		locutor.dibujar(getGame().getBatch(), getGame().getFont());

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
		BitmapFont font = getGame().getFont();
		float transparencia = Math.min(1f, (DURACION_INTRO - tiempoFase) / 30f);

		font.getData().setScale(3.5f);
		font.setColor(1f, 0.85f, 0.2f, transparencia);
		font.draw(getGame().getBatch(), "OLEADA " + nivel.getNumero() + "/" + DirectorNiveles.TOTAL_OLEADAS, 0, Constantes.ALTO / 2 + 140,
				Constantes.ANCHO, Align.center, false);

		font.getData().setScale(2f);
		font.setColor(0.4f, 0.9f, 1f, transparencia);
		font.draw(getGame().getBatch(), nivel.getNombre(), 0, Constantes.ALTO / 2 + 50, Constantes.ANCHO, Align.center, false);

		// Aviso de la oleada (enemigo nuevo, jefe, etc.)
		font.getData().setScale(1.5f);
		font.setColor(1f, 0.6f, 0.2f, transparencia);
		font.draw(getGame().getBatch(), nivel.getAviso(), 0, Constantes.ALTO / 2 - 10, Constantes.ANCHO, Align.center, false);

		// Antes de que entre el primer enemigo: "preparate"; después: "¡a pelear!"
		font.getData().setScale(2f);
		font.setColor(1f, 1f, 1f, transparencia);
		String mensaje = tiempoOleada < RETRASO_ENTRADA ? "¡PREPÁRATE!" : "¡ELIMINA A TODOS!";
		font.draw(getGame().getBatch(), mensaje, 0, Constantes.ALTO / 2 - 90, Constantes.ANCHO, Align.center, false);

		font.getData().setScale(1f);
		font.setColor(0.7f, 0.7f, 0.8f, transparencia);
		font.draw(getGame().getBatch(), "ENTER: saltar", 0, Constantes.ALTO / 2 - 160, Constantes.ANCHO, Align.center, false);

		font.setColor(Color.WHITE);
		font.getData().setScale(1.5f);
	}

	// Oscurece la pantalla y muestra el cartel de pausa
	private void dibujarPausa()
	{
		getGame().getBatch().setColor(0, 0, 0, 0.7f);
		getGame().getBatch().draw(GestorRecursos.getInstance().getPixelBlanco(), 0, 0, Constantes.ANCHO, Constantes.ALTO);
		getGame().getBatch().setColor(Color.WHITE);
		getGame().getFont().getData().setScale(3f);
		getGame().getFont().draw(getGame().getBatch(), "PAUSA - AFK", 0, Constantes.ALTO - 160, Constantes.ANCHO, Align.center, false);
		menuPausa.dibujar(getGame().getBatch(), getGame().getFont());
		getGame().getFont().getData().setScale(1f);
		getGame().getFont().draw(getGame().getBatch(), "P o ESC: continuar", 0, 130, Constantes.ANCHO, Align.center, false);
		getGame().getFont().getData().setScale(1.5f);
	}

	// ---------- PASO 3 del template (gancho): ¿hay que cambiar de pantalla? ----------
	@Override
	protected void revisarTransicion()
	{
		if (accionPausa == 1)
		{
			accionPausa = -1;
			getGame().setScreen(new PantallaOpciones(getGame(), this));
			return;
		}
		if (accionPausa == 2)
		{
			accionPausa = -1;
			getGame().setScreen(new PantallaMenu(getGame()));
			return;
		}

		if (nave.estaDestruido())
		{
			// Se guarda el récord (si corresponde) y se muestra la pantalla de fin
			boolean nuevoRecord = getGame().registrarPuntaje(score);
			getGame().setScreen(new PantallaGameOver(getGame(), score, nuevoRecord));
		}
		else if (fase == Fase.COMPLETADA && tiempoFase >= DURACION_COMPLETADA
				&& nivel.getNumero() >= DirectorNiveles.TOTAL_OLEADAS)
		{
			// Se completó la última oleada: se guarda el récord y se muestra la victoria
			boolean nuevoRecord = getGame().registrarPuntaje(score);
			getGame().setScreen(new PantallaVictoria(getGame(), score, nuevoRecord));
		}
		else if (fase == Fase.COMPLETADA && tiempoFase >= DURACION_COMPLETADA)
		{
			// Oleada completada: el Director arma la siguiente y se sigue con la misma nave
			DirectorNiveles director = new DirectorNiveles(new NivelPartidaBuilder(dificultad));
			getGame().setScreen(new PantallaJuego(getGame(), director.construirOleada(nivel.getNumero() + 1), nave, score, locutor));
		}
	}

	@Override
	public void show()
	{
		GestorRecursos.getInstance().reproducirMusica(pistaDeseada);
	}
}
