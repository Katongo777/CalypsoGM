package puppy.code.modelo.powerups;

import puppy.code.modelo.Nave4;
import puppy.code.util.GestorRecursos;

/*
    La silla gamer: da un escudo durante 10 segundos (600 fotogramas a 60 por segundo).
    Mientras dura, los enemigos que te tocan no te hacen daño.
*/
public class SillaGamer extends PowerUp
{
    public SillaGamer(float x, float y)
    {
        super(x, y, GestorRecursos.getInstance().getTextura("pw_silla.png"));
    }

    @Override
    public void aplicar(Nave4 nave)
    {
        nave.activarEscudo(600);
    }

    @Override
    public String getMensaje()
    {
        return "¡SILLA GAMER! Escudo 10s";
    }
}
