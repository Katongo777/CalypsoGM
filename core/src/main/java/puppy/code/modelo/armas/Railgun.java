package puppy.code.modelo.armas;

import java.util.ArrayList;
import java.util.List;

import puppy.code.modelo.Bullet;
import puppy.code.util.GestorRecursos;

/*
    Estrategia concreta: la Railgun dispara un rayo muy rápido que ATRAVIESA a
    todos los enemigos de su camino. Hace más daño, pero tarda en recargar.
*/
public class Railgun implements EstrategiaDisparo
{
    @Override
    public List<Bullet> disparar(float x, float y)
    {
        GestorRecursos recursos = GestorRecursos.getInstance();
        List<Bullet> balas = new ArrayList<>();

        // atraviesa = true: la bala no se destruye al golpear
        balas.add(new Bullet(x - 6, y, 0, 24, recursos.getTextura("bala_railgun.png"), 12, 48, 2, true));

        recursos.getSonido("pop-sound.mp3").play(0.8f, 1.6f, 0);
        return balas;
    }

    @Override
    public int getCooldown()
    {
        return 45;
    }

    @Override
    public String getNombre()
    {
        return "Railgun";
    }
}
