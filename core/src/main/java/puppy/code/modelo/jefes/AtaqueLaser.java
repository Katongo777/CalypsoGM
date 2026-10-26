package puppy.code.modelo.jefes;

import com.badlogic.gdx.math.MathUtils;

import puppy.code.Constantes;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.Rayo;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    Rayos con aviso: primero aparece una línea roja parpadeante y un momento después cae el
    rayo. El primero siempre se pone justo donde está la nave (hay que moverse); los demás
    caen en lugares al azar.
*/
public class AtaqueLaser implements AtaqueJefe
{
    private final int intervalo;
    private final int cantidad;
    private final boolean horizontal;
    private int frames;

    public AtaqueLaser(int intervalo, int cantidad, boolean horizontal)
    {
        this.intervalo = intervalo;
        this.cantidad = cantidad;
        this.horizontal = horizontal;
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
        for (int i = 0; i < cantidad; i++)
        {
            float posicion;
            if (i == 0)
            {
                posicion = horizontal ? contexto.getNaveCentroY() : contexto.getNaveCentroX();
            }
            else
            {
                posicion = horizontal ? MathUtils.random(120, Constantes.ALTO - 120) : MathUtils.random(80, Constantes.ANCHO - 80);
            }
            contexto.agregarRayo(new Rayo(posicion, horizontal, 75, 40, 70));
        }
        contexto.reproducir("laser.wav");
    }
}
