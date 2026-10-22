package puppy.code.vista;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.SpaceNavigation;
import puppy.code.modelo.Ball2;
import puppy.code.modelo.Bullet;
import puppy.code.modelo.Nave4;
import puppy.code.util.GestorRecursos;


public class PantallaJuego implements Screen
{

	private SpaceNavigation game;
	private OrthographicCamera camera;	
	private SpriteBatch batch;
	private Sound explosionSound;
	private Music gameMusic;
	private int score;
	private int ronda;
	private int velXAsteroides; 
	private int velYAsteroides; 
	private int cantAsteroides;
	
	private Nave4 nave;
	private  ArrayList<Ball2> balls1 = new ArrayList<>();
	private  ArrayList<Ball2> balls2 = new ArrayList<>();
	private  ArrayList<Bullet> balas = new ArrayList<>();


	public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
						int velXAsteroides, int velYAsteroides, int cantAsteroides)
	{
		this.game = game;
		this.ronda = ronda;
		this.score = score;
		this.velXAsteroides = velXAsteroides;
		this.velYAsteroides = velYAsteroides;
		this.cantAsteroides = cantAsteroides;
		
		batch = game.getBatch();
		camera = new OrthographicCamera();	
		camera.setToOrtho(false, 800, 640);
		
		// Los recursos vienen del Singleton: se cargan una sola vez y se reutilizan
		GestorRecursos recursos = GestorRecursos.getInstance();
		explosionSound = recursos.getSonido("explosion.ogg");
		gameMusic = recursos.getMusica();
		gameMusic.play();
		
	    nave = new Nave4(Gdx.graphics.getWidth()/2-50, 30, recursos.getTextura("MainShip3.png"),
	    				recursos.getSonido("hurt.ogg"),
	    				recursos.getTextura("Rocket2.png"),
	    				recursos.getSonido("pop-sound.mp3"));
        nave.setVidas(vidas);
        
        // crear asteroides
        Random r = new Random();
	    for (int i = 0; i < cantAsteroides; i++)
	    {
	        Ball2 bb = new Ball2(r.nextInt((int)Gdx.graphics.getWidth()),
	  	            50+r.nextInt((int)Gdx.graphics.getHeight()-50),
	  	            20+r.nextInt(10), velXAsteroides+r.nextInt(4), velYAsteroides+r.nextInt(4), 
	  	            recursos.getTextura("aGreyMedium4.png"));	   
	  	    balls1.add(bb);
	  	    balls2.add(bb);
	  	}
	}
    
	public void dibujaEncabezado()
	{
		CharSequence str = "Vidas: "+nave.getVidas()+" Ronda: "+ronda;
		game.getFont().getData().setScale(2f);		
		game.getFont().draw(batch, str, 10, 30);
		game.getFont().draw(batch, "Score:"+this.score, Gdx.graphics.getWidth()-150, 30);
		game.getFont().draw(batch, "HighScore:"+game.getHighScore(), Gdx.graphics.getWidth()/2-100, 30);
	}
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		dibujaEncabezado();
	    if (!nave.estaHerido())
	    {
	    	// colisiones entre balas y asteroides y su destruccion  
	    	for (int i = 0; i < balas.size(); i++)
	    	{
	    		Bullet b = balas.get(i);
		        b.actualizar();
		        for (int j = 0; j < balls1.size(); j++)
		        {    
		        	if (b.revisarColisionAsteroide(balls1.get(j)))
		        	{          
		        		explosionSound.play();
		            	balls1.remove(j);
		            	balls2.remove(j);
		            	j--;
		            	score +=10;
		        	}   	  
		        }
		                
		        // b.draw(batch);
		        if (b.isDestroyed())
		        {
		        	balas.remove(b);
		        	i--; //para no saltarse 1 tras eliminar del arraylist
		        }
	    	}
	    	  
	    	//actualizar movimiento de asteroides dentro del área
	    	for (Ball2 ball : balls1)
	    	{
	    		ball.actualizar();
	    	}
		      
	    	//colisiones entre asteroides y sus rebotes  
	    	for (int i=0;i<balls1.size();i++)
	    	{
	    		Ball2 ball1 = balls1.get(i);   
	    		for (int j=0;j<balls2.size();j++)
	    		{
	    			Ball2 ball2 = balls2.get(j); 
	    			if (i<j)
	    			{
	    				ball1.revisarColisionAsteroide(ball2);
	    			}
	    		}
	    	} 
	    }
	      
	    //dibujar balas
	    for (Bullet b : balas)
	    {       
	    	b.dibujar(batch);
	    }
	     
	    nave.actualizar();

	    // La nave crea la bala y la pantalla decide donde guardarla (el modelo ya no conoce la vista)
	    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !nave.estaHerido())
	    {
	    	balas.add(nave.disparar());
	    }
	    nave.dibujar(batch);
	     
	    //dibujar asteroides y manejar colisión con nave
	    for (int i = 0; i < balls1.size(); i++)
	    {
	    	Ball2 b=balls1.get(i);
	    	b.dibujar(batch);
	    	
	    	//perdió vida o game over
	    	if (nave.revisarColision(b))
	    	{
	    		//asteroide se destruye con el choque             
	    		balls1.remove(i);
	    		balls2.remove(i);
	    		i--;
	    	}   	  
	    }
	      
	    if (nave.estaDestruido())
	    {
	    	if (score > game.getHighScore())
	    	{
	    		game.setHighScore(score);
	    	}
	    	
	    	Screen ss = new PantallaGameOver(game);
  			ss.resize(1200, 800);
  			game.setScreen(ss);
  			dispose();
	    }
	    
	    batch.end();
	    
	    //nivel completado
	    if (balls1.size()==0)
	    {
	    	Screen ss = new PantallaJuego(game,ronda+1, nave.getVidas(), score, 
										velXAsteroides+3, velYAsteroides+3, cantAsteroides+10);
			ss.resize(1200, 800);
			game.setScreen(ss);
			dispose();
		  }
	}
	
	@Override
	public void show()
	{
		// TODO Auto-generated method stub
		gameMusic.play();
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose()
	{
		// Los recursos son compartidos (Singleton): se liberan al cerrar el juego
	}
}
