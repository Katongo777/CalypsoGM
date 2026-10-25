package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;

/*
    El enemigo básico: un "bot" que rebota por la pantalla en línea recta.
    Antes esta clase se llamaba Ball2 y era la única; ahora es una más de la
    jerarquía de enemigos (hereda de Enemigo).
*/
public class EnemigoBot extends Enemigo
{
    private int framesParaDisparar = MathUtils.random(120, 300);

    public EnemigoBot(int x, int y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 1, 10); // 1 de vida, da 10 puntos
        setTamano(64, 64);
        ubicarDentroDePantalla();
    }

    @Override
    public String getNombre()
    {
        return "Bot";
    }

    // Se mueve en línea recta y rebota contra los bordes
    @Override
    public void actualizar()
    {
        moverConRebote();
    }

    // Desde la oleada 2 dispara de vez en cuando una bala lenta hacia abajo (cada vez más seguido)
    @Override
    public void atacar(ContextoCombate contexto)
    {
        if (isEntrando() || contexto.getNumeroOleada() < 2)
        {
            return;
        }
        framesParaDisparar--;
        if (framesParaDisparar <= 0)
        {
            framesParaDisparar = contexto.intervalo(Math.max(150, 330 - 8 * contexto.getNumeroOleada())) + MathUtils.random(0, 90);
            contexto.disparar(new BalaEnemiga(getX() + getAncho() / 2, getY() + 10, 0, -4.5f));
        }
    }
}
