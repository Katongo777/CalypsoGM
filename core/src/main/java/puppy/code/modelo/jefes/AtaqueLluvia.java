package puppy.code.modelo.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.Constantes;
import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    Una lluvia: balas que caen desde arriba en posiciones al azar.
*/
public class AtaqueLluvia implements AtaqueJefe
{
    private final int intervalo;
    private final float rapidez;
    private final Color color;
    private int frames;

    public AtaqueLluvia(int intervalo, float rapidez, Color color)
    {
        this.intervalo = intervalo;
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
        frames = contexto.intervaloRapido(intervalo);
        float x = MathUtils.random(30, Constantes.ANCHO - 30);
        contexto.disparar(new BalaEnemiga(x, Constantes.ALTO - 10, 0, -rapidez).conColor(color));
    }
}
