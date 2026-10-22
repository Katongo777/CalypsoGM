package puppy.code.modelo;

import com.badlogic.gdx.graphics.Texture;

import puppy.code.Constantes;
import puppy.code.modelo.enemigos.Enemigo;

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
        setPosicion(getX() + getXVel(), getY() + getYVel());
        comprobarLimitesPantalla();
    }

    // Método privado para ocultar la validación de los bordes de la pantalla
    private void comprobarLimitesPantalla()
    {
        if (getX() < 0 || getX() + getAncho() > Constantes.ANCHO)
        {
            destruir();
        }
        if (getY() < 0 || getY() + getAlto() > Constantes.ALTO)
        {
            destruir();
        }
    }

    // Funciona con cualquier tipo de Enemigo (polimorfismo)
    public boolean revisarColision(Enemigo enemigo)
    {
        if (this.chocaCon(enemigo))
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
