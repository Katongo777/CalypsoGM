package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Bullet
{
	private int xSpeed;
	private int ySpeed;
	private boolean destroyed = false;
	private Sprite spr;
	    
	public Bullet(float x, float y, int xSpeed, int ySpeed, Texture tx)
	{
		spr = new Sprite(tx);
		spr.setPosition(x, y);
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}
	    
	// Solo actualiza la posición de la bala
	public void actualizar()
	{
		spr.setPosition(spr.getX() + xSpeed, spr.getY() + ySpeed);
		comprobarLimitesPantalla();
	}
	    
	// Método privado para ocultar la validación de los bordes de la pantalla
	private void comprobarLimitesPantalla()
	{
		if (spr.getX() < 0 || spr.getX() + spr.getWidth() > Gdx.graphics.getWidth())
		{
			destruir();
		}
		if (spr.getY() < 0 || spr.getY() + spr.getHeight() > Gdx.graphics.getHeight())
		{
			destruir();
		}
	}
	
	// Método para renderizar Bullet
    public void dibujar(SpriteBatch batch)
    {
        spr.draw(batch);
    }
    
    // El retorno booleano indica a la pantalla si hubo impacto
    public boolean revisarColisionAsteroide(Ball2 b2)
    {
        if (spr.getBoundingRectangle().overlaps(b2.getArea()))
        {
            destruir();
            return true;
        }
        return false;
    }

    private void destruir()
    {
        this.destroyed = true;
    }
    
    public boolean isDestroyed()
    { 
        return destroyed; 
    }
}
