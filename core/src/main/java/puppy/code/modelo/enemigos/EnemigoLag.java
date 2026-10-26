package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/*
    El "Lag": pequeño, rápido y molesto. Es una burbuja roja con el ping en 999, y de vez
    en cuando pega un "lag spike": se teletransporta un poco. Justo antes de hacerlo
    tiembla y parpadea, para que el jugador pueda esquivarlo.

    Sobrescribe actualizar() con su propio comportamiento (polimorfismo): la
    pantalla solo llama enemigo.actualizar() sin saber que este es distinto.
*/
public class EnemigoLag extends Enemigo
{
    private static final int FRAMES_AVISO = 30; // cuánto tiembla antes de teletransportarse

    private int framesParaSalto;

    public EnemigoLag(float x, float y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 1, 15); // 1 de vida, da 15 puntos
        setTamano(64, 48);
        setFactorHitbox(0.7f);
        ubicarDentroDePantalla();
        framesParaSalto = MathUtils.random(60, 150);
    }

    @Override
    public String getNombre()
    {
        return "Lag";
    }

    @Override
    public Color getColorExplosion()
    {
        return new Color(1f, 0.3f, 0.3f, 1f);
    }

    @Override
    public void actualizar()
    {
        moverConRebote();
        if (isEntrando())
        {
            return; // todavía está entrando: no hace lag spikes
        }

        framesParaSalto--;
        if (framesParaSalto <= 0)
        {
            // Lag spike: salta de golpe a otra posición cercana
            setPosicion(getX() + MathUtils.random(-120, 120), getY() + MathUtils.random(-120, 120));
            ubicarDentroDePantalla();
            framesParaSalto = MathUtils.random(60, 150);
        }
    }

    // Antes del salto se dibuja temblando y parpadeando (efecto "glitch")
    @Override
    public void dibujar(SpriteBatch batch)
    {
        boolean avisando = !isEntrando() && framesParaSalto <= FRAMES_AVISO;
        if (!avisando)
        {
            super.dibujar(batch);
            return;
        }
        float x = getX();
        float y = getY();
        setPosicion(x + MathUtils.random(-4, 4), y + MathUtils.random(-4, 4));
        setTinte(1f, 1f, 1f, framesParaSalto % 6 < 3 ? 0.45f : 1f);
        super.dibujar(batch);
        setTinte(1f, 1f, 1f, 1f);
        setPosicion(x, y);
    }
}
