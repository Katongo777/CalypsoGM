package puppy.code.vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;

/*
    El "locutor" gamer: lleva la cuenta de las eliminaciones seguidas (la racha) y
    anuncia mensajes en pantalla, como en los juegos de disparos:
    FIRST BLOOD, COMBO, KILLING SPREE...
    Si te hacen daño, la racha se corta y el locutor se burla un poquito.
*/
public class Locutor
{
    private static final int DURACION = 100; // fotogramas que dura un mensaje en pantalla

    private static final String[] FRASES_DANIO = {
        "Skill issue", "F", "Eso fue lag, lo juro", "Ouch", "Te falta ping", "Noob detected"
    };

    private int racha = 0;
    private boolean primeraSangre = true;
    private String mensaje = "";
    private int tiempo = 0;

    // Se llama cada vez que el jugador elimina a un enemigo
    public void registrarEliminacion()
    {
        racha++;
        if (primeraSangre)
        {
            primeraSangre = false;
            anunciar("¡FIRST BLOOD!");
        }
        else if (racha == 3)
        {
            anunciar("¡COMBO x3!");
        }
        else if (racha == 5)
        {
            anunciar("¡KILLING SPREE!");
        }
        else if (racha == 8)
        {
            anunciar("¡RAMPAGE!");
        }
        else if (racha == 12)
        {
            anunciar("¡GODLIKE!");
        }
    }

    // Se llama cuando el jugador recibe daño: se corta la racha
    public void registrarDanio()
    {
        racha = 0;
        anunciar(FRASES_DANIO[MathUtils.random(FRASES_DANIO.length - 1)]);
    }

    // Muestra un mensaje cualquiera durante unos segundos
    public void anunciar(String texto)
    {
        mensaje = texto;
        tiempo = DURACION;
    }

    public void actualizar()
    {
        if (tiempo > 0)
        {
            tiempo--;
        }
    }

    public void dibujar(SpriteBatch batch, BitmapFont font)
    {
        if (tiempo <= 0)
        {
            return;
        }
        // El mensaje se va desvaneciendo en los últimos fotogramas
        float transparencia = Math.min(1f, tiempo / 30f);
        font.getData().setScale(2f);
        font.setColor(1f, 0.85f, 0.2f, transparencia);
        font.draw(batch, mensaje, 0, Constantes.ALTO - 120, Constantes.ANCHO, Align.center, false);
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
    }
}
