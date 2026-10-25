package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.Constantes;
import puppy.code.modelo.ContextoCombate;
import puppy.code.util.GestorRecursos;

/*
    El JEFE: un enemigo grande con mucha vida que patrulla por arriba de la pantalla
    y cada cierto tiempo invoca Lags. Cuando le queda la mitad de la vida se enoja:
    se mueve más rápido e invoca más seguido.

    Es otro hijo de Enemigo (herencia + polimorfismo): PantallaJuego lo trata como
    a cualquier enemigo, pero él sobrescribe los métodos gancho para comportarse distinto:
      - actualizar(): su propio movimiento
      - atacar(): invoca Lags
      - seDestruyeAlChocar(): false, no desaparece cuando choca con la nave
      - sueltaPowerUpSeguro(): true, siempre deja un premio al morir
*/
public class EnemigoJefe extends Enemigo
{
    private static final int FRAMES_ENTRE_INVOCACIONES = 240;
    private static final int FRAMES_ENTRE_INVOCACIONES_ENOJADO = 150;

    private final String nombre;
    private final int vidaMaxima;
    private final float velocidadBase;
    private int framesParaInvocar = FRAMES_ENTRE_INVOCACIONES;
    private int framesDestello = 0; // mientras es mayor que 0 el jefe se dibuja rojizo (recibió un golpe)
    private int tiempo = 0;

    public EnemigoJefe(TipoJefe tipo, Texture tx)
    {
        super(0, 0, tipo.getVelocidad(), 0, tx, tipo.getVida(), tipo.getPuntos());
        this.nombre = tipo.getNombre();
        this.vidaMaxima = tipo.getVida();
        this.velocidadBase = tipo.getVelocidad();
        setTamano(tx.getWidth() * 4, tx.getHeight() * 4); // los sprites son pixel-art: se agrandan x4
        setFactorHitbox(0.85f);
    }

    @Override
    public String getNombre()
    {
        return nombre;
    }

    public int getVidaMaxima()
    {
        return vidaMaxima;
    }

    // Con la mitad de la vida o menos entra en "modo enojado"
    public boolean estaEnojado()
    {
        return getVida() * 2 <= vidaMaxima;
    }

    @Override
    public void recibirDano(float danio)
    {
        super.recibirDano(danio);
        framesDestello = 6;
    }

    @Override
    public void actualizar()
    {
        tiempo++;
        if (framesDestello > 0)
        {
            framesDestello--;
        }

        if (isEntrando())
        {
            moverConRebote(); // baja desde arriba como los demás
            return;
        }

        // Patrulla de lado a lado; enojado se mueve 1,5 veces más rápido
        float rapidez = estaEnojado() ? velocidadBase * 1.5f : velocidadBase;
        float direccion = getXVel() >= 0 ? 1 : -1;
        setXVel(direccion * rapidez);

        float x = getX() + getXVel();
        if (x < 0)
        {
            x = 0;
            setXVel(rapidez);
        }
        else if (x + getAncho() > Constantes.ANCHO)
        {
            x = Constantes.ANCHO - getAncho();
            setXVel(-rapidez);
        }
        // Baja despacio hasta su altura de patrulla y flota suavemente arriba y abajo
        float objetivo = Constantes.ALTO - getAlto() - 80 + (float) Math.sin(tiempo / 25.0) * 10;
        float y = getY() + (objetivo - getY()) * 0.08f;
        setPosicion(x, y);
    }

    // Cada cierto tiempo invoca dos Lags desde su posición (enojado, más seguido)
    @Override
    public void atacar(ContextoCombate contexto)
    {
        if (isEntrando())
        {
            return;
        }
        framesParaInvocar--;
        if (framesParaInvocar <= 0)
        {
            framesParaInvocar = estaEnojado() ? FRAMES_ENTRE_INVOCACIONES_ENOJADO : FRAMES_ENTRE_INVOCACIONES;
            Texture txLag = GestorRecursos.getInstance().getTextura("lag.png");
            float x = getX() + getAncho() / 2;
            contexto.agregarEnemigo(new EnemigoLag(x, getY(), 3, -3, txLag));
            contexto.agregarEnemigo(new EnemigoLag(x, getY(), -3, -3, txLag));
        }
    }

    @Override
    public boolean empujable()
    {
        return false;
    }

    @Override
    public boolean seDestruyeAlChocar()
    {
        return false;
    }

    @Override
    public boolean sueltaPowerUpSeguro()
    {
        return true;
    }

    // Se tiñe de rojo un instante cuando recibe un golpe
    @Override
    public void dibujar(SpriteBatch batch)
    {
        if (framesDestello > 0)
        {
            batch.setColor(1f, 0.45f, 0.45f, 1f);
        }
        super.dibujar(batch);
        batch.setColor(Color.WHITE);
    }
}
