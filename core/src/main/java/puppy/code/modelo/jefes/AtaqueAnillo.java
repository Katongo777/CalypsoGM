package puppy.code.modelo.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    Dispara un anillo de balas hacia todos lados desde el centro del jefe.
    (El ataque "Salto" del Hacker también lo usa al aparecer en su nueva posición.)
*/
public class AtaqueAnillo implements AtaqueJefe
{
    private final int intervalo;
    private final int cantidad;
    private final float rapidez;
    private final Color color;
    private int frames;

    public AtaqueAnillo(int intervalo, int cantidad, float rapidez, Color color)
    {
        this.intervalo = intervalo;
        this.cantidad = cantidad;
        this.rapidez = rapidez;
        this.color = color;
        this.frames = intervalo;
    }

    @Override
    public void actualizar(EnemigoJefe jefe, ContextoCombate contexto)
    {
        frames--;
        if (frames > 0)
        {
            return;
        }
        frames = contexto.intervalo(intervalo);
        disparar(jefe, contexto, cantidad, rapidez, color);
    }

    // Reutilizable: dispara un anillo ahora mismo
    public static void disparar(EnemigoJefe jefe, ContextoCombate contexto, int cantidad, float rapidez, Color color)
    {
        float x = jefe.getX() + jefe.getAncho() / 2;
        float y = jefe.getY() + jefe.getAlto() / 2;
        float giro = MathUtils.random(MathUtils.PI2); // cada anillo empieza en un ángulo distinto
        for (int i = 0; i < cantidad; i++)
        {
            float angulo = giro + MathUtils.PI2 * i / cantidad;
            contexto.disparar(new BalaEnemiga(x, y, MathUtils.cos(angulo) * rapidez, MathUtils.sin(angulo) * rapidez)
                    .conColor(color));
        }
    }
}
