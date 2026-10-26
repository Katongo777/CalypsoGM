package puppy.code.vista;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.Constantes;
import puppy.code.util.GestorRecursos;

/*
    Fondo de estrellas que caen lentamente (efecto de "viajar por el espacio").
    Cada estrella es un cuadradito: las más rápidas son más grandes y brillantes,
    lo que da sensación de profundidad. No usa imágenes, solo un píxel blanco
    que se estira (viene del Singleton GestorRecursos).
*/
public class FondoEstrellado
{
    // Clase interna: los datos de una estrella
    private static class Estrella
    {
        float x;
        float y;
        float velocidad;
        float tamano;
    }

    private ArrayList<Estrella> estrellas = new ArrayList<>();
    private Texture pixel;

    public FondoEstrellado()
    {
        pixel = GestorRecursos.getInstance().getPixelBlanco();
        for (int i = 0; i < 110; i++)
        {
            Estrella e = new Estrella();
            e.x = MathUtils.random(Constantes.ANCHO);
            e.y = MathUtils.random(Constantes.ALTO);
            e.velocidad = MathUtils.random(0.3f, 2.2f);
            e.tamano = e.velocidad > 1.5f ? 4 : (e.velocidad > 0.9f ? 3 : 2);
            estrellas.add(e);
        }
    }

    public void actualizar()
    {
        for (Estrella e : estrellas)
        {
            e.y -= e.velocidad;
            if (e.y < 0)
            {
                // Sale por abajo: reaparece arriba en otra posición
                e.y = Constantes.ALTO;
                e.x = MathUtils.random(Constantes.ANCHO);
            }
        }
    }

    public void dibujar(SpriteBatch batch)
    {
        for (Estrella e : estrellas)
        {
            float brillo = 0.3f + e.velocidad / 3f;
            batch.setColor(brillo, brillo, 1f, 1f);
            batch.draw(pixel, e.x, e.y, e.tamano, e.tamano);
        }
        batch.setColor(Color.WHITE); // dejar el batch como estaba
    }
}
