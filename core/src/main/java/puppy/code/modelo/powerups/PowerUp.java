package puppy.code.modelo.powerups;

import com.badlogic.gdx.graphics.Texture;

import puppy.code.modelo.Entidad;
import puppy.code.modelo.Nave4;

/*
    CLASE ABSTRACTA (GM-4): el "padre" de todos los power-ups.

    Un power-up cae desde donde murió un enemigo y, si la nave lo toca, le da un
    efecto. Aquí está lo común (caer, desaparecer al salir de la pantalla); cada
    hijo define su propio efecto en aplicar().

    POLIMORFISMO: PantallaJuego guarda una lista de PowerUp y llama a aplicar() sin
    saber si es una Pizza, una Silla Gamer o una RGB.
*/
public abstract class PowerUp extends Entidad
{
    protected PowerUp(float x, float y, Texture tx)
    {
        super(x, y, 0, -2f, tx); // cae despacio hacia abajo
        setTamano(48, 48);
    }

    // Cada power-up da un efecto distinto a la nave
    public abstract void aplicar(Nave4 nave);

    // Mensaje que el locutor muestra al recogerlo
    public abstract String getMensaje();

    @Override
    public void actualizar()
    {
        setPosicion(getX() + getXVel(), getY() + getYVel());
    }

    // Se salió por abajo sin que lo recogieran
    public boolean salioDePantalla()
    {
        return getY() + getAlto() < 0;
    }
}
