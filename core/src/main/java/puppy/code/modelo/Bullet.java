package puppy.code.modelo;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;

import puppy.code.Constantes;
import puppy.code.modelo.enemigos.Enemigo;

/*
    Una bala. Cada bala sabe cuánto daño hace, qué tamaño tiene y si atraviesa
    a los enemigos (como la Railgun) o desaparece al golpear a uno.
*/
public class Bullet extends Entidad
{
    private boolean destroyed = false;
    private float danio;
    private boolean atraviesa;
    // Enemigos que esta bala ya golpeó (para no dañar al mismo enemigo en cada fotograma)
    private ArrayList<Enemigo> golpeados = new ArrayList<>();

    // SOBRECARGA DE CONSTRUCTORES: sin indicar "atraviesa", la bala es normal
    public Bullet(float x, float y, float xVel, float yVel, Texture tx, float ancho, float alto, float danio)
    {
        this(x, y, xVel, yVel, tx, ancho, alto, danio, false);
    }

    public Bullet(float x, float y, float xVel, float yVel, Texture tx, float ancho, float alto, float danio,
                  boolean atraviesa)
    {
        super(x, y, xVel, yVel, tx);
        setTamano(ancho, alto);
        this.danio = danio;
        this.atraviesa = atraviesa;
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

    /*
        Funciona con cualquier tipo de Enemigo (polimorfismo). Devuelve true si hubo
        un golpe nuevo. Una bala normal se destruye; una que atraviesa sigue de largo.
    */
    public boolean revisarColision(Enemigo enemigo)
    {
        if (!golpeados.contains(enemigo) && this.chocaCon(enemigo))
        {
            golpeados.add(enemigo);
            if (!atraviesa)
            {
                destruir();
            }
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

    public float getDanio()
    {
        return danio;
    }

    // Multiplica el daño de la bala (por ejemplo 0.5 = la mitad)
    public void escalarDanio(float factor)
    {
        danio = danio * factor;
    }
}
