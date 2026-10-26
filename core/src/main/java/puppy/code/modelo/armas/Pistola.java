package puppy.code.modelo.armas;

import java.util.ArrayList;
import java.util.List;

import puppy.code.modelo.Bullet;
import puppy.code.util.GestorRecursos;

/*
    Arma básica (estrategia concreta): una bala recta, cadencia normal.
*/
public class Pistola implements EstrategiaDisparo
{
    @Override
    public List<Bullet> disparar(float x, float y)
    {
        GestorRecursos recursos = GestorRecursos.getInstance();
        List<Bullet> balas = new ArrayList<>();

        // La imagen original es de 4x8 píxeles; se dibuja 3 veces más grande (12x24)
        balas.add(new Bullet(x - 6, y, 0, 10, recursos.getTextura("bala_pistola.png"), 12, 24, 1));

        recursos.reproducirSonido("pop-sound.mp3", 0.6f, 1.0f);
        return balas;
    }

    @Override
    public int getCooldown()
    {
        return 12;
    }

    @Override
    public String getNombre()
    {
        return "Pistola";
    }
}
