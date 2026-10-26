package puppy.code.vista;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    Cuando pierdes una vida, el corazón se ROMPE: se parte en dos mitades que saltan y caen.
*/
public class Hud
{
    // Las dos mitades de un corazón roto, que caen mientras se desvanecen
    private static class CorazonRoto
    {
        float x, y, velX, velY;
        int edad = 0;
    }

    private static final int DURACION_ROTO = 50;

    private Texture corazon;
    private TextureRegion mitadIzquierda;
    private TextureRegion mitadDerecha;
    private int vidasPrevias = -1;
    private final ArrayList<CorazonRoto> rotos = new ArrayList<>();

    public Hud()
    {
        corazon = GestorRecursos.getInstance().getTextura("corazon.png");
        // la imagen del corazón mide 9x8: se corta en una mitad de 4 y otra de 5 píxeles de ancho
        mitadIzquierda = new TextureRegion(corazon, 0, 0, 4, 8);
        mitadDerecha = new TextureRegion(corazon, 4, 0, 5, 8);
    }

    public void dibujar(SpriteBatch batch, BitmapFont font, Nave4 nave, Nivel nivel, int score, int record, int enemigosRestantes)
    {
        font.getData().setScale(1.5f);

        // Vidas: un corazón por cada vida (la imagen es de 9x8 y se dibuja 3 veces más grande)
        int vidas = Math.max(0, nave.getVidas());
        detectarVidasPerdidas(vidas);
        boolean peligro = vidas == 1; // con una sola vida el corazón late
        for (int i = 0; i < Math.min(vidas, 8); i++)
        {
            float latido = peligro ? 1f + 0.12f * (float) Math.sin(System.currentTimeMillis() / 120.0) : 1f;
            batch.draw(corazon, 10 + i * 34 - (latido - 1f) * 13, 8 - (latido - 1f) * 12, 27 * latido, 24 * latido);
        }
        if (vidas > 8)
        {
            font.draw(batch, "x" + vidas, 10 + 8 * 34, 44);
        }
        dibujarCorazonesRotos(batch);

        dibujarEnergia(batch, font, nave);

        font.draw(batch, "Oleada " + nivel.getNumero() + "/" + DirectorNiveles.TOTAL_OLEADAS + "  |  "
                + nave.getNombreArma(), 380, 44);
        font.draw(batch, "Enemigos: " + enemigosRestantes, 10, Constantes.ALTO - 8);
        font.draw(batch, "Récord: " + record, Constantes.ANCHO / 2 + 215, 44);
        font.draw(batch, "Score: " + score, Constantes.ANCHO - 190, 44);
    }

    // Si hay menos vidas que antes, rompe los corazones que se perdieron
    private void detectarVidasPerdidas(int vidas)
    {
        if (vidasPrevias < 0)
        {
            vidasPrevias = vidas;
        }
        for (int i = vidas; i < Math.min(vidasPrevias, 8); i++)
        {
            CorazonRoto c = new CorazonRoto();
            c.x = 10 + i * 34;
            c.y = 8;
            c.velY = 6f;
            rotos.add(c);
        }
        vidasPrevias = vidas;
    }

    private void dibujarCorazonesRotos(SpriteBatch batch)
    {
        for (int i = 0; i < rotos.size(); i++)
        {
            CorazonRoto c = rotos.get(i);
            c.edad++;
            c.velY -= 0.35f;      // gravedad
            c.y += c.velY;
            c.velX = 1.6f;
            float alfa = 1f - (float) c.edad / DURACION_ROTO;
            batch.setColor(1f, 1f, 1f, Math.max(0f, alfa));
            // izquierda: se va hacia la izquierda y gira hacia un lado; derecha: al revés
            batch.draw(mitadIzquierda, c.x - c.velX * c.edad, c.y, 6, 12, 12, 24, 1, 1, c.edad * 5);
            batch.draw(mitadDerecha, c.x + 12 + c.velX * c.edad, c.y, 7, 12, 15, 24, 1, 1, -c.edad * 5);
            if (c.edad >= DURACION_ROTO)
            {
                rotos.remove(i);
                i--;
            }
        }
        batch.setColor(Color.WHITE);
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
