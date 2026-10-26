package puppy.code.modelo.jefes;

import com.badlogic.gdx.graphics.Texture;

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
    public enum Apoyo { BOT, LAG, CHEATER, SPAMMER, SNIPER }

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
            Enemigo e = crear(centro + lado - 30, jefe.getY(), i % 2 == 0 ? 3 : -3);
            contexto.agregarEnemigo(e);
            jefe.registrarApoyo(e);
        }
    }

    private Enemigo crear(float x, float y, float xVel)
    {
        GestorRecursos recursos = GestorRecursos.getInstance();
        switch (tipo)
        {
            case BOT:
                return new EnemigoBot((int) x, (int) y, xVel, -2.5f, recursos.getTextura("bot.png"));
            case CHEATER:
                return new EnemigoCheater(x, y, xVel * 0.5f, -1.5f, recursos.getTextura("cheater.png"));
            case SPAMMER:
                return new EnemigoSpammer(x, y, xVel, 0, recursos.getTextura("spammer.png"));
            case SNIPER:
                return new EnemigoSniper(x, y, xVel * 0.4f, -1f, recursos.getTextura("sniper.png"));
            default:
                Texture lag = recursos.getTextura("lag.png");
                return new EnemigoLag(x, y, xVel, -3, lag);
        }
    }
}
