package puppy.code.modelo.jefes;

import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    El jefe se pone rojo y tiembla (aviso) y después se lanza en picada hacia abajo,
    y vuelve a su lugar. Quien esté en su camino pierde una vida.
*/
public class AtaqueEmbestida implements AtaqueJefe
{
    private final int intervalo;
    private int frames;

    public AtaqueEmbestida(int intervalo)
    {
        this.intervalo = intervalo;
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
        jefe.iniciarEmbestida(contexto.getNaveCentroX());
    }
}
