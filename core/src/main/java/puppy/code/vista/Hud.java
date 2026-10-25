package puppy.code.vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.Constantes;
import puppy.code.modelo.Nave4;
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

    public void dibujar(SpriteBatch batch, BitmapFont font, Nave4 nave, Nivel nivel, int score, int record)
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

        font.draw(batch, "Oleada " + nivel.getNumero() + "  |  " + nave.getNombreArma(), 300, 44);
        font.draw(batch, "Récord: " + record, Constantes.ANCHO / 2 + 190, 44);
        font.draw(batch, "Score: " + score, Constantes.ANCHO - 190, 44);
    }
}
