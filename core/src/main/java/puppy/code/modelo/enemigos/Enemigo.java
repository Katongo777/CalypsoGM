package puppy.code.modelo.enemigos;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import puppy.code.Constantes;
import puppy.code.modelo.ContextoCombate;
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
    private float vida;
    private int puntos;
    private boolean entrando = false; // true mientras baja desde arriba al empezar la oleada

    protected Enemigo(float x, float y, float xVel, float yVel, Texture tx, int vida, int puntos)
    {
        super(x, y, xVel, yVel, tx);
        this.vida = vida;
        this.puntos = puntos;
        setFactorHitbox(0.8f);
    }

    private static final float VELOCIDAD_ENTRADA = 4f;

    /*
        Hace que el enemigo aparezca por arriba de la pantalla en la posición x y baje hasta
        entrar. Mientras entra no choca con nada (así la entrada no es injusta).
    */
    public void iniciarEntrada(float x)
    {
        setPosicion(Math.max(0, Math.min(x, Constantes.ANCHO - getAncho())), Constantes.ALTO + 20);
        entrando = true;
    }

    public boolean isEntrando()
    {
        return entrando;
    }

    private static final float VELOCIDAD_ENTRADA = 4f;

    /*
        Hace que el enemigo aparezca por arriba de la pantalla en la posición x y baje hasta
        entrar. Mientras entra no choca con nada (así la entrada no es injusta).
    */
    public void iniciarEntrada(float x)
    {
        setPosicion(Math.max(0, Math.min(x, Constantes.ANCHO - getAncho())), Constantes.ALTO + 20);
        entrando = true;
    }

    public boolean isEntrando()
    {
        return entrando;
    }

    // Cada tipo de enemigo define su propio nombre
    public abstract String getNombre();

    public void recibirDano(float danio)
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

    /*
        Más métodos gancho, con comportamiento normal por defecto. Los hijos (como el
        jefe) los sobrescriben para cambiarlo sin que PantallaJuego tenga que saber de qué tipo es.
    */

    /*
        El ataque del enemigo: se llama en cada fotograma mientras la oleada está en juego.
        Por defecto no hace nada; los hijos lo sobrescriben para disparar o invocar enemigos
        usando el ContextoCombate (ahí dejan sus balas y sus enemigos nuevos).
    */
    public void atacar(ContextoCombate contexto)
    {
    }

    // ¿Desaparece cuando choca con la nave? Los normales sí; el jefe no
    public boolean seDestruyeAlChocar()
    {
        return true;
    }

    // ¿Siempre deja un power-up al morir (sin depender de la suerte)?
    public boolean sueltaPowerUpSeguro()
    {
        return false;
    }

    // Si el enemigo quedó (parcialmente) fuera de la ventana, lo vuelve a meter
    protected void ubicarDentroDePantalla()
    {
        float x = Math.max(0, Math.min(getX(), Constantes.ANCHO - getAncho()));
        float y = Math.max(Constantes.ZONA_ENEMIGOS_MIN_Y, Math.min(getY(), Constantes.ALTO - getAlto()));
        setPosicion(x, y);
    }

    // Mueve al enemigo y lo hace rebotar contra los bordes de la ventana
    protected void moverConRebote()
    {
        // Entrando: baja en línea recta hasta quedar dentro de la pantalla
        if (entrando)
        {
            setPosicion(getX(), getY() - VELOCIDAD_ENTRADA);
            if (getY() + getAlto() <= Constantes.ALTO - 10)
            {
                entrando = false;
            }
            return;
        }

        setPosicion(getX() + getXVel(), getY() + getYVel());

        // Al tocar un borde se corrige la posición y se manda la velocidad HACIA ADENTRO.
        // (Antes solo se invertía la velocidad: si un enemigo quedaba pasado del borde, la
        // invertía en cada fotograma y se quedaba atrapado fuera de la pantalla para siempre.)
        if (getX() < 0)
        {
            setPosicion(0, getY());
            setXVel(Math.abs(getXVel()));
        }
        else if (getX() + getAncho() > Constantes.ANCHO)
        {
            setPosicion(Constantes.ANCHO - getAncho(), getY());
            setXVel(-Math.abs(getXVel()));
        }
        if (getY() < Constantes.ZONA_ENEMIGOS_MIN_Y)
        {
            setPosicion(getX(), Constantes.ZONA_ENEMIGOS_MIN_Y);
            setYVel(Math.abs(getYVel()));
        }
        else if (getY() + getAlto() > Constantes.ALTO)
        {
            setPosicion(getX(), Constantes.ALTO - getAlto());
            setYVel(-Math.abs(getYVel()));
        }
    }

    // ¿Se lo puede empujar? Los enemigos normales sí; el jefe es "pesado" y no se mueve por choques
    public boolean empujable()
    {
        return true;
    }

    /*
        Choque entre enemigos. Antes solo se invertía la velocidad de los dos, pero seguían
        superpuestos en el fotograma siguiente, se volvían a invertir y quedaban pegados
        vibrando. Ahora primero se SEPARAN (cada uno se corre la mitad de lo que se pisan)
        y después cada uno sale rebotando en dirección contraria al otro.
    */
    public void revisarColisionEnemigo(Enemigo otro)
    {
        if (!empujable() || !otro.empujable())
        {
            return;
        }
        Rectangle a = getArea();
        Rectangle b = otro.getArea();
        if (!a.overlaps(b))
        {
            return;
        }

        // cuánto se pisan en cada eje
        float pisaX = Math.min(a.x + a.width, b.x + b.width) - Math.max(a.x, b.x);
        float pisaY = Math.min(a.y + a.height, b.y + b.height) - Math.max(a.y, b.y);

        if (pisaX < pisaY)
        {
            float empuje = pisaX / 2 + 1;
            float signo = a.x < b.x ? -1 : 1; // -1: yo me voy a la izquierda
            setPosicion(getX() + signo * empuje, getY());
            otro.setPosicion(otro.getX() - signo * empuje, otro.getY());
            setXVel(signo * Math.max(Math.abs(getXVel()), 1f));
            otro.setXVel(-signo * Math.max(Math.abs(otro.getXVel()), 1f));
        }
        else
        {
            float empuje = pisaY / 2 + 1;
            float signo = a.y < b.y ? -1 : 1; // -1: yo me voy hacia abajo
            setPosicion(getX(), getY() + signo * empuje);
            otro.setPosicion(otro.getX(), otro.getY() - signo * empuje);
            setYVel(signo * Math.max(Math.abs(getYVel()), 1f));
            otro.setYVel(-signo * Math.max(Math.abs(otro.getYVel()), 1f));
        }
    }

    public float getVida() { return vida; }
    public int getPuntos() { return puntos; }
}
