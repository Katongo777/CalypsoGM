package puppy.code.modelo.jefes;

import com.badlogic.gdx.graphics.Color;

import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    Dispara una o varias balas (en abanico) APUNTANDO a donde está la nave en ese momento.
*/
public class AtaqueApuntado implements AtaqueJefe
{
    private final int intervalo;
    private final int cantidad;
    private final float rapidez;
    private final Color color;
    private int frames;

    public AtaqueApuntado(int intervalo, int cantidad, float rapidez, Color color)
    {
        this.intervalo = intervalo;
        this.cantidad = cantidad;
        this.rapidez = rapidez;
        this.color = color;
        this.frames = intervalo; // la primera vez espera un intervalo completo
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
        float x = jefe.getX() + jefe.getAncho() / 2;
        float y = jefe.getY() + 20;
        for (int i = 0; i < cantidad; i++)
        {
            float desvio = (i - (cantidad - 1) / 2f) * 0.22f;
            contexto.disparar(BalaEnemiga.apuntada(x, y, contexto.getNaveCentroX(), contexto.getNaveCentroY(), rapidez, desvio)
                    .conColor(color));
        }
    }
}
