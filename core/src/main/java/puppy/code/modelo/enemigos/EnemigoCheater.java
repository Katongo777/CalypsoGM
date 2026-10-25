package puppy.code.modelo.enemigos;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;

import puppy.code.util.GestorRecursos;

/*
    El "Cheater": grande, lento y resistente (3 de vida). Cuando por fin lo
    eliminas, se descubre el cheat y suelta 2 Lags.

    Para eso sobrescribe el método "gancho" alMorir() de Enemigo.
*/
public class EnemigoCheater extends Enemigo
{
    private int framesParaDisparar = MathUtils.random(150, 300);

    public EnemigoCheater(float x, float y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 3, 50); // 3 de vida, da 50 puntos
        setTamano(96, 96);
        ubicarDentroDePantalla();
    }

    @Override
    public String getNombre()
    {
        return "Cheater";
    }

    @Override
    public void actualizar()
    {
        moverConRebote();
    }

    // Dispara 3 balas en abanico APUNTANDO a la nave ("aimbot")
    @Override
    public void atacar(ContextoCombate contexto)
    {
        if (isEntrando())
        {
            return;
        }
        framesParaDisparar--;
        if (framesParaDisparar <= 0)
        {
            framesParaDisparar = contexto.intervalo(Math.max(170, 290 - 5 * contexto.getNumeroOleada())) + MathUtils.random(0, 60);
            float x = getX() + getAncho() / 2;
            float y = getY() + 10;
            for (float desvio : new float[] { -0.25f, 0f, 0.25f })
            {
                contexto.disparar(BalaEnemiga.apuntada(x, y, contexto.getNaveCentroX(), contexto.getNaveCentroY(), 4.2f, desvio)
                        .conColor(new Color(0.85f, 0.6f, 1f, 1f)));
            }
        }
    }

    // Al morir se parte en dos Lags que salen disparados en direcciones opuestas
    @Override
    public List<Enemigo> alMorir()
    {
        Texture txLag = GestorRecursos.getInstance().getTextura("lag.png");
        List<Enemigo> hijos = new ArrayList<>();
        hijos.add(new EnemigoLag(getX(), getY(), 3, 3, txLag));
        hijos.add(new EnemigoLag(getX(), getY(), -3, -3, txLag));
        return hijos;
    }
}
