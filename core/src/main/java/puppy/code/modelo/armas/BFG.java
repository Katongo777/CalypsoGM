package puppy.code.modelo.armas;

import java.util.ArrayList;
import java.util.List;

import puppy.code.modelo.Bullet;
import puppy.code.util.GestorRecursos;

/*
    Estrategia concreta: el BFG 9000 (homenaje a los shooters clásicos).
    Dispara una esfera de energía ENORME y lenta que atraviesa todo y hace 5 de
    daño: elimina hasta a un Cheater de un solo golpe. Tarda bastante en recargar.
*/
public class BFG implements EstrategiaDisparo
{
    @Override
    public List<Bullet> disparar(float x, float y)
    {
        GestorRecursos recursos = GestorRecursos.getInstance();
        List<Bullet> balas = new ArrayList<>();

        // La imagen es de 16x16 píxeles y se dibuja 5 veces más grande (80x80)
        balas.add(new Bullet(x - 40, y, 0, 5, recursos.getTextura("bala_bfg.png"), 80, 80, 5, true));

        recursos.reproducirSonido("explosion.ogg", 0.8f, 0.6f);
        return balas;
    }

    @Override
    public int getCooldown()
    {
        return 60;
    }

    @Override
    public String getNombre()
    {
        return "BFG 9000";
    }
}
