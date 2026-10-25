package puppy.code.modelo.powerups;

import puppy.code.modelo.Nave4;
import puppy.code.util.GestorRecursos;

/*
    El teclado RGB: "todo el mundo sabe que con luces RGB se juega mejor".
    Durante 10 segundos la nave dispara al doble de velocidad.
*/
public class RGB extends PowerUp
{
    public RGB(float x, float y)
    {
        super(x, y, GestorRecursos.getInstance().getTextura("pw_rgb.png"));
    }

    @Override
    public void aplicar(Nave4 nave)
    {
        nave.activarTurbo(600);
    }

    @Override
    public String getMensaje()
    {
        return "¡RGB ON! Disparo x2 por 10s";
    }
}
