package puppy.code.modelo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import puppy.code.Constantes;
import puppy.code.util.GestorRecursos;

/*
    Un rayo de los jefes: una franja que cruza toda la pantalla (vertical u horizontal).

    Funciona en dos tiempos:
      1. AVISO: una línea delgada parpadeante muestra por dónde va a caer (no hace daño)
      2. ACTIVO: el rayo se ensancha y SÍ daña a la nave si lo toca

    Implementa Colisionable (como Entidad) para que la nave pueda revisar el choque igual
    que con cualquier otra cosa. No es una Entidad porque no tiene imagen ni velocidad.
*/
public class Rayo implements Colisionable
{
    private final float posicion;      // x si es vertical, y si es horizontal
    private final boolean horizontal;
    private final float grosor;
    private int framesAviso;
    private int framesActivo;

    public Rayo(float posicion, boolean horizontal, int framesAviso, int framesActivo, float grosor)
    {
        this.posicion = posicion;
        this.horizontal = horizontal;
        this.framesAviso = framesAviso;
        this.framesActivo = framesActivo;
        this.grosor = grosor;
    }

    public void actualizar()
    {
        if (framesAviso > 0)
        {
            framesAviso--;
        }
        else if (framesActivo > 0)
        {
            framesActivo--;
        }
    }

    public boolean estaActivo() { return framesAviso == 0 && framesActivo > 0; }

    public boolean estaTerminado() { return framesAviso == 0 && framesActivo == 0; }

    // Mientras solo es un aviso no tiene área de choque
    @Override
    public Rectangle getArea()
    {
        if (!estaActivo())
        {
            return new Rectangle(-1000, -1000, 1, 1);
        }
        if (horizontal)
        {
            return new Rectangle(0, posicion - grosor / 2, Constantes.ANCHO, grosor);
        }
        return new Rectangle(posicion - grosor / 2, 0, grosor, Constantes.ALTO);
    }

    @Override
    public boolean chocaCon(Colisionable otro)
    {
        return estaActivo() && getArea().overlaps(otro.getArea());
    }

    public void dibujar(SpriteBatch batch)
    {
        Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
        if (!estaActivo())
        {
            // Aviso: línea fina que parpadea cada vez más rápido
            boolean visible = (framesAviso / (framesAviso > 25 ? 6 : 3)) % 2 == 0;
            batch.setColor(1f, 0.25f, 0.25f, visible ? 0.9f : 0.3f);
            dibujarFranja(batch, pixel, 6);
        }
        else
        {
            // Activo: franja ancha con el centro blanco
            batch.setColor(1f, 0.3f, 0.3f, 0.85f);
            dibujarFranja(batch, pixel, grosor);
            batch.setColor(1f, 1f, 1f, 1f);
            dibujarFranja(batch, pixel, grosor * 0.45f);
        }
        batch.setColor(Color.WHITE);
    }

    private void dibujarFranja(SpriteBatch batch, Texture pixel, float ancho)
    {
        if (horizontal)
        {
            batch.draw(pixel, 0, posicion - ancho / 2, Constantes.ANCHO, ancho);
        }
        else
        {
            batch.draw(pixel, posicion - ancho / 2, 0, ancho, Constantes.ALTO);
        }
    }
}
