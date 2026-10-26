package puppy.code.vista;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.util.GestorRecursos;

/*
    Un sistema de partículas muy simple: cuadraditos de colores que salen disparados desde un
    punto (explosiones, chispas) y se van achicando y desvaneciendo. Se dibujan con el
    píxel blanco del Singleton, igual que las estrellas, así no necesitan imágenes.
*/
public class Particulas
{
    private static class Particula
    {
        float x, y, velX, velY, tamano;
        int vida, vidaTotal;
        Color color;
    }

    private final ArrayList<Particula> lista = new ArrayList<>();

    // Crea una explosión de "cantidad" cuadraditos que salen en todas direcciones
    public void explosion(float x, float y, Color color, int cantidad, float rapidez)
    {
        for (int i = 0; i < cantidad; i++)
        {
            Particula p = new Particula();
            float angulo = MathUtils.random(MathUtils.PI2);
            float velocidad = MathUtils.random(0.3f, 1f) * rapidez;
            p.x = x;
            p.y = y;
            p.velX = MathUtils.cos(angulo) * velocidad;
            p.velY = MathUtils.sin(angulo) * velocidad;
            p.tamano = MathUtils.random(4f, 9f);
            p.vidaTotal = MathUtils.random(18, 38);
            p.vida = p.vidaTotal;
            p.color = color;
            lista.add(p);
        }
    }

    public void actualizar()
    {
        for (int i = 0; i < lista.size(); i++)
        {
            Particula p = lista.get(i);
            p.x += p.velX;
            p.y += p.velY;
            p.velX *= 0.95f;
            p.velY *= 0.95f;
            p.vida--;
            if (p.vida <= 0)
            {
                lista.remove(i);
                i--;
            }
        }
    }

    public void dibujar(SpriteBatch batch)
    {
        Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
        for (Particula p : lista)
        {
            float vivo = (float) p.vida / p.vidaTotal;
            batch.setColor(p.color.r, p.color.g, p.color.b, vivo);
            float t = p.tamano * (0.4f + 0.6f * vivo);
            batch.draw(pixel, p.x - t / 2, p.y - t / 2, t, t);
        }
        batch.setColor(Color.WHITE);
    }
}
