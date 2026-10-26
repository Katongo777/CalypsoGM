package puppy.code.vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.modelo.Nave4;
import puppy.code.modelo.niveles.DirectorNiveles;
import puppy.code.modelo.niveles.Nivel;
import puppy.code.util.GestorRecursos;

/*
    El HUD (los datos que se muestran abajo mientras juegas). Está en su propia
    clase para que PantallaJuego no se llene de código de dibujo: cada clase hace
    una sola cosa (responsabilidad única).
*/
public class Hud
{
    private Texture corazon;

    public Hud()
    {
        corazon = GestorRecursos.getInstance().getTextura("corazon.png");
    }

    public void dibujar(SpriteBatch batch, BitmapFont font, Nave4 nave, Nivel nivel, int score, int record, int enemigosRestantes)
    {
        font.getData().setScale(1.5f);

        // Vidas: un corazón por cada vida (la imagen es de 9x8 y se dibuja 3 veces más grande)
        int vidas = Math.max(0, nave.getVidas());
        for (int i = 0; i < Math.min(vidas, 8); i++)
        {
            batch.draw(corazon, 10 + i * 34, 8, 27, 24);
        }
        if (vidas > 8)
        {
            font.draw(batch, "x" + vidas, 10 + 8 * 34, 44);
        }

        dibujarEnergia(batch, font, nave);

        font.draw(batch, "Oleada " + nivel.getNumero() + "/" + DirectorNiveles.TOTAL_OLEADAS + "  |  "
                + nave.getNombreArma(), 380, 44);
        font.draw(batch, "Enemigos: " + enemigosRestantes, 10, Constantes.ALTO - 8);
        font.draw(batch, "Récord: " + record, Constantes.ANCHO / 2 + 215, 44);
        font.draw(batch, "Score: " + score, Constantes.ANCHO - 190, 44);
    }

    // La barra de la habilidad especial: se llena con las bajas y parpadea cuando se puede usar (X)
    private void dibujarEnergia(SpriteBatch batch, BitmapFont font, Nave4 nave)
    {
        Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
        float x = 10;
        float y = 40;
        float ancho = 250;
        float alto = 22;
        boolean lista = nave.habilidadLista();
        boolean destello = lista && (System.currentTimeMillis() / 250) % 2 == 0;

        batch.setColor(destello ? Color.WHITE : new Color(0.5f, 0.55f, 0.8f, 1f));
        batch.draw(pixel, x - 3, y - 3, ancho + 6, alto + 6);
        batch.setColor(0.05f, 0.05f, 0.15f, 1f);
        batch.draw(pixel, x, y, ancho, alto);
        batch.setColor(lista ? new Color(1f, 0.85f, 0.2f, 1f) : new Color(0.3f, 0.7f, 1f, 1f));
        batch.draw(pixel, x, y, ancho * nave.getEnergia() / Nave4.ENERGIA_MAXIMA, alto);
        batch.setColor(Color.WHITE);

        font.getData().setScale(1f);
        String texto = lista ? "X: " + nave.getHabilidad().getNombre().toUpperCase() + "!"
                : nave.getHabilidad().getNombre().toUpperCase() + " " + Math.round(nave.getEnergia()) + "%";
        font.draw(batch, texto, x, y + alto / 2 + 11, ancho, Align.center, false);
        font.getData().setScale(1.5f);
    }
}
