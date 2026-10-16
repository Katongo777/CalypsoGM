package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;


public class Bullet extends Entidad
{
	private boolean destroyed = false;
	    
	public Bullet(float x, float y, float xVel, float yVel, Texture tx)
	{
		super(x, y, xVel, yVel, tx);
	}
	    
	// Solo actualiza la posición de la bala
	@Override
	public void actualizar()
	{
		spr.setPosition(spr.getX() + xVel, spr.getY() + yVel);
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
    
    // El retorno booleano indica a la pantalla si hubo impacto
    public boolean revisarColisionAsteroide(Ball2 b2)
    {
        if (this.chocaCon(b2))
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
