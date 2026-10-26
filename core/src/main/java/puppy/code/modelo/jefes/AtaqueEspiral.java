package puppy.code.modelo.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    Dispara una espiral continua: cada pocos fotogramas sale una bala (por cada "brazo")
    con un ángulo un poquito distinto, así se forman remolinos de balas.
*/
public class AtaqueEspiral implements AtaqueJefe
{
    private final int cadaFrames;
    private final int brazos;
    private final float rapidez;
    private final Color color;
    private int frames;
    private float angulo = 0;

    public AtaqueEspiral(int cadaFrames, int brazos, float rapidez, Color color)
    {
        this.cadaFrames = cadaFrames;
        this.brazos = brazos;
        this.rapidez = rapidez;
        this.color = color;
        this.frames = cadaFrames * 4;
    }

    @Override
    public void actualizar(EnemigoJefe jefe, ContextoCombate contexto)
    {
        frames--;
        if (frames > 0)
        {
            return;
        }
        frames = contexto.intervaloRapido(cadaFrames);
        float x = jefe.getX() + jefe.getAncho() / 2;
        float y = jefe.getY() + jefe.getAlto() / 2;
        for (int i = 0; i < brazos; i++)
        {
            float a = angulo + MathUtils.PI2 * i / brazos;
            contexto.disparar(new BalaEnemiga(x, y, MathUtils.cos(a) * rapidez, MathUtils.sin(a) * rapidez).conColor(color));
        }
        angulo += 0.28f;
    }
}
