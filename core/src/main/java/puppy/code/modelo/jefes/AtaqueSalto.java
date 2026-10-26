package puppy.code.modelo.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.Constantes;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    El "lag spike" del Hacker: se teletransporta a otro lugar y al llegar suelta un anillo de balas.
*/
public class AtaqueSalto implements AtaqueJefe
{
    private final int intervalo;
    private final int balasAlLlegar;
    private final Color color;
    private int frames;

    public AtaqueSalto(int intervalo, int balasAlLlegar, Color color)
    {
        this.intervalo = intervalo;
        this.balasAlLlegar = balasAlLlegar;
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
        jefe.teletransportar(MathUtils.random(40, Constantes.ANCHO - jefe.getAncho() - 40));
        AtaqueAnillo.disparar(jefe, contexto, balasAlLlegar, 3.5f, color);
    }
}
