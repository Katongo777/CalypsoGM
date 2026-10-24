package puppy.code.modelo.armas;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;

import puppy.code.modelo.Bullet;
import puppy.code.util.GestorRecursos;

/*
    Estrategia concreta: la Escopeta dispara 3 perdigones en abanico.
    Cadencia lenta, pero cubre más espacio.
*/
public class Escopeta implements EstrategiaDisparo
{
    @Override
    public List<Bullet> disparar(float x, float y)
    {
        GestorRecursos recursos = GestorRecursos.getInstance();
        Texture tx = recursos.getTextura("bala_escopeta.png");
        List<Bullet> balas = new ArrayList<>();

        // Tres balas: una a la izquierda, una recta y una a la derecha
        float[] desviacion = { -3f, 0f, 3f };
        for (float dx : desviacion)
        {
            balas.add(new Bullet(x - 6, y, dx, 9, tx, 12, 12, 1));
        }

        recursos.getSonido("pop-sound.mp3").play(0.7f, 0.7f, 0);
        return balas;
    }

    @Override
    public int getCooldown()
    {
        return 28;
    }

    @Override
    public String getNombre()
    {
        return "Escopeta";
    }
}
