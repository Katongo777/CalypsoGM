package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;
import puppy.code.util.GestorRecursos;

/*
    El "Sniper" (aparece desde la oleada 11): un camper con casco que se mueve despacio.
    De vez en cuando se detiene y APUNTA: muestra una línea roja parpadeante hacia donde estaba
    tu nave, y un momento después dispara una bala rápida a ese punto. Si te mueves a tiempo,
    la esquivas. Tiene 2 de vida y da 30 puntos.
*/
public class EnemigoSniper extends Enemigo
{
    private static final int FRAMES_APUNTANDO = 70;

    private int framesParaApuntar = MathUtils.random(120, 240);
    private int framesApuntando = 0;   // mayor que 0 mientras está apuntando
    private float objetivoX;           // el punto donde apuntó (no sigue a la nave)
    private float objetivoY;

    public EnemigoSniper(float x, float y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 2, 30);
        setTamano(56, 56);
        ubicarDentroDePantalla();
    }

    @Override
    public String getNombre()
    {
        return "Sniper";
    }

    @Override
    public void actualizar()
    {
        if (framesApuntando > 0 && !isEntrando())
        {
            return; // mientras apunta se queda quieto
        }
        moverConRebote();
    }

    @Override
    public void atacar(ContextoCombate contexto)
    {
        if (isEntrando())
        {
            return;
        }
        float cx = getX() + getAncho() / 2;
        float cy = getY() + getAlto() / 2;

        if (framesApuntando > 0)
        {
            framesApuntando--;
            if (framesApuntando == 0)
            {
                contexto.disparar(BalaEnemiga.apuntada(cx, cy, objetivoX, objetivoY, 9f, 0)
                        .conColor(new Color(1f, 0.35f, 0.35f, 1f)));
                framesParaApuntar = contexto.intervalo(260) + MathUtils.random(0, 80);
            }
            return;
        }

        framesParaApuntar--;
        if (framesParaApuntar <= 0)
        {
            framesApuntando = FRAMES_APUNTANDO;
            objetivoX = contexto.getNaveCentroX();
            objetivoY = contexto.getNaveCentroY();
        }
    }

    // Mientras apunta dibuja una línea roja parpadeante hasta el punto elegido
    @Override
    public void dibujar(SpriteBatch batch)
    {
        super.dibujar(batch);
        if (framesApuntando > 0 && !isEntrando())
        {
            float cx = getX() + getAncho() / 2;
            float cy = getY() + getAlto() / 2;
            float angulo = MathUtils.atan2(objetivoY - cy, objetivoX - cx) * MathUtils.radiansToDegrees;
            Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
            batch.setColor(1f, 0.2f, 0.2f, (framesApuntando / 5) % 2 == 0 ? 0.9f : 0.35f);
            batch.draw(pixel, cx, cy - 1.5f, 0, 1.5f, 1500, 3, 1, 1, angulo, 0, 0, 1, 1, false, false);
            batch.setColor(Color.WHITE);
        }
    }
}
