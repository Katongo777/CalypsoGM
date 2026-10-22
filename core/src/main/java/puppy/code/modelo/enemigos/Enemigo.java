package puppy.code.modelo.enemigos;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import puppy.code.modelo.Entidad;

/*
    JERARQUÍA DE CLASES: CLASE ABSTRACTA + POLIMORFISMO (GM-4)

    Enemigo hereda de Entidad y es, a su vez, el padre de todos los tipos de
    enemigos (EnemigoBot, EnemigoLag, EnemigoCheater...). Aquí va lo que tienen
    en común: la vida, los puntos que dan y cómo rebotan.

    Es abstracta porque no existe un "enemigo genérico": siempre es de algún
    tipo concreto. Por eso no se puede hacer "new Enemigo(...)".

    POLIMORFISMO: PantallaJuego guarda una lista de Enemigo y llama a
    actualizar() en todos; cada hijo responde a su manera, sin que la pantalla
    tenga que saber de qué tipo es.
*/
public abstract class Enemigo extends Entidad
{
    private int vida;
    private int puntos;

    protected Enemigo(float x, float y, float xVel, float yVel, Texture tx, int vida, int puntos)
    {
        super(x, y, xVel, yVel, tx);
        this.vida = vida;
        this.puntos = puntos;
    }

    // Cada tipo de enemigo define su propio nombre
    public abstract String getNombre();

    public void recibirDano(int danio)
    {
        vida = vida - danio;
    }

    public boolean estaMuerto()
    {
        return vida <= 0;
    }

    /*
        Método "gancho": por defecto un enemigo no deja nada al morir.
        Los hijos pueden sobrescribirlo para devolver enemigos nuevos
        (por ejemplo, uno grande que se parte en dos pequeños).
    */
    public List<Enemigo> alMorir()
    {
        return new ArrayList<>();
    }

    // Mueve al enemigo y lo hace rebotar contra los bordes de la ventana
    protected void moverConRebote()
    {
        setPosicion(getX() + getXVel(), getY() + getYVel());

        // Al tocar un borde se corrige la posición y se manda la velocidad HACIA ADENTRO.
        // (Antes solo se invertía la velocidad: si un enemigo quedaba pasado del borde, la
        // invertía en cada fotograma y se quedaba atrapado fuera de la pantalla para siempre.)
        if (getX() < 0)
        {
            setPosicion(0, getY());
            setXVel(Math.abs(getXVel()));
        }
        else if (getX() + getAncho() > Gdx.graphics.getWidth())
        {
            setPosicion(Gdx.graphics.getWidth() - getAncho(), getY());
            setXVel(-Math.abs(getXVel()));
        }
        if (getY() < 0)
        {
            setPosicion(getX(), 0);
            setYVel(Math.abs(getYVel()));
        }
        else if (getY() + getAlto() > Gdx.graphics.getHeight())
        {
            setPosicion(getX(), Gdx.graphics.getHeight() - getAlto());
            setYVel(-Math.abs(getYVel()));
        }
    }

    // Cuando choca con otro enemigo no manipula sus variables, solo le avisa del choque
    public void revisarColisionEnemigo(Enemigo otro)
    {
        if (this.chocaCon(otro))
        {
            float miXVel = getXVel();
            float miYVel = getYVel();

            this.reaccionarChoque(otro.getXVel(), otro.getYVel());
            otro.reaccionarChoque(miXVel, miYVel);
        }
    }

    // El enemigo es responsable de cambiar su propia velocidad al chocar
    public void reaccionarChoque(float xVelOtro, float yVelOtro)
    {
        if (getXVel() == 0) setXVel(getXVel() + xVelOtro / 2);
        setXVel(-getXVel());

        if (getYVel() == 0) setYVel(getYVel() + yVelOtro / 2);
        setYVel(-getYVel());
    }

    public int getVida() { return vida; }
    public int getPuntos() { return puntos; }
}
