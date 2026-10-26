package puppy.code.modelo.powerups;

import puppy.code.modelo.Nave4;
import puppy.code.util.GestorRecursos;

/*
    La pizza: el clásico power-up de curación. Devuelve una vida.
*/
public class Pizza extends PowerUp
{
    public Pizza(float x, float y)
    {
        super(x, y, GestorRecursos.getInstance().getTextura("pw_pizza.png"));
    }

    @Override
    public void aplicar(Nave4 nave)
    {
        nave.curar(1);
    }

    @Override
    public String getMensaje()
    {
        return "¡PIZZA! +1 vida";
    }
}
