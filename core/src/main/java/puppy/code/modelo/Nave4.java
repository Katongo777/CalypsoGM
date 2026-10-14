package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.vista.PantallaJuego;



public class Nave4
{
	private boolean destruida = false;
    private int vidas = 3;
    private float xVel = 0;
    private float yVel = 0;
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax=50;
    private int tiempoHerido;
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala)
    {
    	sonidoHerido = soundChoque;
    	this.soundBala = soundBala;
    	this.txBala = txBala;
    	spr = new Sprite(tx);
    	spr.setPosition(x, y);
    	//spr.setOriginCenter();
    	spr.setBounds(x, y, 45, 45);

    }
    
    // Este método solo procesa datos y estado
    public void actualizar(PantallaJuego juego)
    {
        if (!herido)
        {
            manejarInputTeclado(juego);
            actualizarPosicion();
        }
        else
        {
            actualizarEstadoHerido();
        }
    }
    // Método privado para ocultar el funcionamiento de la nave
    private void manejarInputTeclado(PantallaJuego juego)
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) xVel--;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) xVel++;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) yVel--;     
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) yVel++;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            disparar(juego);
        }
    }
    
    private void disparar(PantallaJuego juego)
    {
        Bullet bala = new Bullet(spr.getX() + spr.getWidth() / 2 - 5, spr.getY() + spr.getHeight() - 5, 0, 3, txBala);
        juego.agregarBala(bala);
        soundBala.play();
    }
    
    private void actualizarPosicion()
    {
        float x = spr.getX();
        float y = spr.getY();
        
        if (x + xVel < 0 || x + xVel + spr.getWidth() > Gdx.graphics.getWidth())
        {
            xVel *= -1;
        }
        if (y + yVel < 0 || y + yVel + spr.getHeight() > Gdx.graphics.getHeight())
        {
            yVel *= -1;
        }
        spr.setPosition(x + xVel, y + yVel);   
    }
    
    private void actualizarEstadoHerido()
    {
        tiempoHerido--;
        if (tiempoHerido <= 0)
        {
            herido = false;
        }
    } 
    
    // Delegamos la responsabilidad a Ball
    public boolean revisarColision(Ball2 b)
    {
        if (!herido && b.getArea().overlaps(spr.getBoundingRectangle())) {
            
            xVel = -xVel;
            yVel = -yVel;
            
            b.reaccionarChoqueNave(xVel, yVel); 
            
            recibirDano();
            return true;
        }
        return false;
    }
    
    // Agrupa toda la lógica de recibir daño
    private void recibirDano()
    {
        vidas--;
        herido = true;
        tiempoHerido = tiempoHeridoMax;
        sonidoHerido.play();
        if (vidas <= 0)
        {
            destruida = true;
        }
    }
      
    // Método para renderizar Nave
    public void dibujar(SpriteBatch batch)
    {
        if (herido)
        {
            float xOriginal = spr.getX();
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch); 
            spr.setX(xOriginal);
        }
        else
        {
            spr.draw(batch);
        }
    }
    
    // Getters y Setters
    public boolean estaDestruido() { return !herido && destruida; }
    public boolean estaHerido() { return herido; }
    public int getVidas() { return vidas; }
    public void setVidas(int vidas) { this.vidas = vidas; }
    public int getX() { return (int) spr.getX(); }
    public int getY() { return (int) spr.getY(); }
}
