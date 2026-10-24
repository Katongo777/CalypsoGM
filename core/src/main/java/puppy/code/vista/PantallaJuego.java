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
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.enemigos.EnemigoBot;
import puppy.code.modelo.enemigos.EnemigoCheater;
import puppy.code.modelo.enemigos.EnemigoLag;
import puppy.code.util.GestorRecursos;

/*
    Pantalla donde se juega. Hereda de PantallaBase (Template Method), así que
    solo define los tres pasos: actualizar (lógica), dibujar y revisarTransicion.
*/
public class PantallaJuego extends PantallaBase
{
	private Sound explosionSound;
	private Music gameMusic;
	private int score;
	private int ronda;
	private int velXAsteroides;
	private int velYAsteroides;
	private int cantAsteroides;

	private Nave4 nave;
	// Una sola lista para todos los enemigos
	private ArrayList<Enemigo> enemigos = new ArrayList<>();
	private ArrayList<Bullet> balas = new ArrayList<>();


	public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
						int velXAsteroides, int velYAsteroides, int cantAsteroides)
	{
		super(game);
		this.ronda = ronda;
		this.score = score;
		this.velXAsteroides = velXAsteroides;
		this.velYAsteroides = velYAsteroides;
		this.cantAsteroides = cantAsteroides;

		// Los recursos vienen del Singleton: se cargan una sola vez y se reutilizan
		GestorRecursos recursos = GestorRecursos.getInstance();
		explosionSound = recursos.getSonido("explosion.ogg");
		gameMusic = recursos.getMusica();
		gameMusic.play();

	    nave = new Nave4(Constantes.ANCHO / 2 - 32, 30, recursos.getTextura("MainShip3.png"),
	    				recursos.getSonido("hurt.ogg"));
        nave.setVidas(vidas);

        // Mezcla provisoria de enemigos (cuando exista el Builder esto lo define un Nivel)
        crearEnemigos(recursos);
	}

	// Crea una mezcla de bots, lags y cheaters según la cantidad pedida
	private void crearEnemigos(GestorRecursos recursos)
	{
		Random r = new Random();
		int cantBots = cantAsteroides / 2;
		int cantLags = cantAsteroides / 4;
		int cantCheaters = Math.max(1, cantAsteroides / 10);

		for (int i = 0; i < cantBots; i++)
		{
			enemigos.add(new EnemigoBot(r.nextInt(Constantes.ANCHO), 250 + r.nextInt(Constantes.ALTO - 250),
					velXAsteroides + r.nextInt(4), velYAsteroides + r.nextInt(4), recursos.getTextura("bot.png")));
		}
		for (int i = 0; i < cantLags; i++)
		{
			enemigos.add(new EnemigoLag(r.nextInt(Constantes.ANCHO), 250 + r.nextInt(Constantes.ALTO - 250),
					velXAsteroides + 2 + r.nextInt(4), velYAsteroides + 2 + r.nextInt(4), recursos.getTextura("lag.png")));
		}
		for (int i = 0; i < cantCheaters; i++)
		{
			enemigos.add(new EnemigoCheater(r.nextInt(Constantes.ANCHO), 250 + r.nextInt(Constantes.ALTO - 250),
					velXAsteroides, velYAsteroides, recursos.getTextura("cheater.png")));
		}
	}

	// ---------- PASO 1 del template: actualizar la lógica ----------
	@Override
	protected void actualizar(float delta)
	{
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
	        			enemigos.remove(j);
	        			enemigos.addAll(enemigo.alMorir()); // por ejemplo, el Cheater suelta 2 Lags
	        		}
	        		break; // una bala solo golpea a un enemigo
	        	}
	        }

	        if (b.isDestroyed())
	        {
	        	balas.remove(b);
	        	i--; //para no saltarse 1 tras eliminar del arraylist
	        }
    	}

    	//actualizar movimiento de enemigos dentro del área
    	for (Enemigo enemigo : enemigos)
    	{
    		enemigo.actualizar();
    	}

    	//colisiones entre enemigos y sus rebotes (cada pareja se revisa una sola vez)
    	for (int i = 0; i < enemigos.size(); i++)
    	{
    		for (int j = i + 1; j < enemigos.size(); j++)
    		{
    			enemigos.get(i).revisarColisionEnemigo(enemigos.get(j));
    		}
    	}

	    //colisión de los enemigos con la nave
	    for (int i = 0; i < enemigos.size(); i++)
	    {
	    	//perdió vida o game over
	    	if (nave.revisarColision(enemigos.get(i)))
	    	{
	    		//el enemigo se destruye con el choque
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
		CharSequence str = "Vidas: "+nave.getVidas()+" Ronda: "+ronda+"  Arma: "+nave.getNombreArma();
		game.getFont().getData().setScale(2f);
		game.getFont().draw(game.getBatch(), str, 10, 30);
		game.getFont().draw(game.getBatch(), "Score:"+this.score, Constantes.ANCHO - 150, 30);
		game.getFont().draw(game.getBatch(), "Récord:"+game.getHighScore(), Constantes.ANCHO / 2 - 60, 30);
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
	    	//nivel completado
	    	game.setScreen(new PantallaJuego(game, ronda + 1, nave.getVidas(), score,
										velXAsteroides + 3, velYAsteroides + 3, cantAsteroides + 10));
	    }
	}

	@Override
	public void show()
	{
		gameMusic.play();
	}
}
