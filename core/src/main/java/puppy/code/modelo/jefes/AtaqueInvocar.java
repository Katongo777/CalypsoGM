package puppy.code.modelo.jefes;


import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.enemigos.EnemigoBot;
import puppy.code.modelo.enemigos.EnemigoCheater;
import puppy.code.modelo.enemigos.EnemigoJefe;
import puppy.code.modelo.enemigos.EnemigoLag;
import puppy.code.modelo.enemigos.EnemigoSniper;
import puppy.code.modelo.enemigos.EnemigoSpammer;
import puppy.code.util.GestorRecursos;

/*
    El jefe invoca enemigos de apoyo desde su posición. Se puede repetir cada cierto tiempo o
    hacerse una sola vez (por ejemplo, durante el escudo).

    Los enemigos invocados se le avisan al jefe (registrarApoyo) para que sepa cuándo ya
    fueron derrotados todos y se rompa su escudo.
*/
public class AtaqueInvocar implements AtaqueJefe
{
    // Qué tipo de enemigo se invoca
    /*
        Qué tipo de enemigo se invoca. Cada valor sabe crear a su enemigo (método distinto en
        cada uno), así este ataque no necesita un switch: un apoyo nuevo es solo un valor más.
    */
    public enum Apoyo
    {
        BOT
        {
            @Override
            Enemigo crear(float x, float y, float xVel)
            {
                return new EnemigoBot((int) x, (int) y, xVel, -2.5f, GestorRecursos.getInstance().getTextura("bot.png"));
            }
        },
        LAG
        {
            @Override
            Enemigo crear(float x, float y, float xVel)
            {
                return new EnemigoLag(x, y, xVel, -3, GestorRecursos.getInstance().getTextura("lag.png"));
            }
        },
        CHEATER
        {
            @Override
            Enemigo crear(float x, float y, float xVel)
            {
                return new EnemigoCheater(x, y, xVel * 0.5f, -1.5f, GestorRecursos.getInstance().getTextura("cheater.png"));
            }
        },
        SPAMMER
        {
            @Override
            Enemigo crear(float x, float y, float xVel)
            {
                return new EnemigoSpammer(x, y, xVel, 0, GestorRecursos.getInstance().getTextura("spammer.png"));
            }
        },
        SNIPER
        {
            @Override
            Enemigo crear(float x, float y, float xVel)
            {
                return new EnemigoSniper(x, y, xVel * 0.4f, -1f, GestorRecursos.getInstance().getTextura("sniper.png"));
            }
        };

        abstract Enemigo crear(float x, float y, float xVel);
    }

    private final int primeraEn;
    private final int intervalo;       // 0 = una sola vez
    private final Apoyo tipo;
    private final int cantidad;
    private int frames;
    private boolean yaInvoco = false;

    public AtaqueInvocar(int primeraEn, int intervalo, Apoyo tipo, int cantidad)
    {
        this.primeraEn = primeraEn;
        this.intervalo = intervalo;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.frames = primeraEn;
    }

    @Override
    public void actualizar(EnemigoJefe jefe, ContextoCombate contexto)
    {
        if (intervalo == 0 && yaInvoco)
        {
            return;
        }
        frames--;
        if (frames > 0)
        {
            return;
        }
        frames = intervalo == 0 ? primeraEn : contexto.intervalo(intervalo);
        yaInvoco = true;

        float centro = jefe.getX() + jefe.getAncho() / 2;
        for (int i = 0; i < cantidad; i++)
        {
            float lado = (i % 2 == 0 ? 1 : -1) * (40 + 50 * (i / 2));
            Enemigo e = tipo.crear(centro + lado - 30, jefe.getY(), i % 2 == 0 ? 3 : -3);
            contexto.agregarEnemigo(e);
            jefe.registrarApoyo(e);
        }
    }
}
