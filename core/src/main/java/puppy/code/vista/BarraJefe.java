package puppy.code.vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.modelo.enemigos.EnemigoJefe;
import puppy.code.util.GestorRecursos;

/*
    La barra de vida del jefe (arriba de la pantalla, con su nombre).
    Está separada de PantallaJuego para que esa clase no se llene de código de dibujo.
*/
public class BarraJefe
{
    private static final float ANCHO_BARRA = 600;
    private static final float ALTO_BARRA = 22;

    public void dibujar(SpriteBatch batch, BitmapFont font, EnemigoJefe jefe)
    {
        Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
        float x = (Constantes.ANCHO - ANCHO_BARRA) / 2;
        float y = Constantes.ALTO - 62;
        float porcentaje = Math.max(0f, jefe.getVida() / jefe.getVidaMaxima());

        // marco, fondo y relleno (se pone amarilla y roja a medida que le queda menos vida)
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(pixel, x - 4, y - 4, ANCHO_BARRA + 8, ALTO_BARRA + 8);
        batch.setColor(0.15f, 0.05f, 0.05f, 1f);
        batch.draw(pixel, x, y, ANCHO_BARRA, ALTO_BARRA);
        if (porcentaje > 0.5f)
        {
            batch.setColor(0.2f, 0.85f, 0.3f, 1f);
        }
        else if (jefe.estaEnojado() && porcentaje > 0.25f)
        {
            batch.setColor(1f, 0.75f, 0.1f, 1f);
        }
        else
        {
            batch.setColor(0.95f, 0.2f, 0.2f, 1f);
        }
        batch.draw(pixel, x, y, ANCHO_BARRA * porcentaje, ALTO_BARRA);
        batch.setColor(Color.WHITE);

        font.getData().setScale(1.5f);
        font.draw(batch, "JEFE: " + jefe.getNombre(), 0, Constantes.ALTO - 8, Constantes.ANCHO, Align.center, false);
    }
}
