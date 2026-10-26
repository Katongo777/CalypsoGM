package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.util.GestorRecursos;

/*
    Una lista de botones que se maneja con el MOUSE y con el TECLADO:
      - con el mouse: al pasar por encima se resalta el botón y con un clic se acciona
      - con el teclado: flechas para moverse y ENTER para accionar

    Los usan el menú, las opciones, la pausa y la guía, así no repetimos el mismo
    código en cada pantalla (cada clase hace una sola cosa).
*/
public class MenuBotones
{
    private final String[] etiquetas;
    private final Rectangle[] areas;
    private final boolean horizontal;
    private int seleccion = 0;
    private float ultimoMouseX = -1;
    private float ultimoMouseY = -1;

    /*
        (x, y) es la esquina inferior izquierda del primer botón. Si la lista es vertical,
        los demás van hacia abajo; si es horizontal, hacia la derecha.
    */
    public MenuBotones(String[] etiquetas, float x, float y, float ancho, float alto, float separacion,
            boolean horizontal)
    {
        this.etiquetas = etiquetas;
        this.horizontal = horizontal;
        this.areas = new Rectangle[etiquetas.length];
        for (int i = 0; i < etiquetas.length; i++)
        {
            if (horizontal)
            {
                areas[i] = new Rectangle(x + i * (ancho + separacion), y, ancho, alto);
            }
            else
            {
                areas[i] = new Rectangle(x, y - i * (alto + separacion), ancho, alto);
            }
        }
    }

    /*
        Revisa teclado y mouse. Devuelve el número del botón que se accionó
        (0, 1, 2...) o -1 si en este fotograma no se accionó ninguno.
    */
    public int actualizar(Viewport viewport)
    {
        int seleccionAntes = seleccion;
        int anterior = horizontal ? Input.Keys.LEFT : Input.Keys.UP;
        int siguiente = horizontal ? Input.Keys.RIGHT : Input.Keys.DOWN;
        if (Gdx.input.isKeyJustPressed(anterior))
        {
            seleccion = (seleccion + etiquetas.length - 1) % etiquetas.length;
        }
        if (Gdx.input.isKeyJustPressed(siguiente))
        {
            seleccion = (seleccion + 1) % etiquetas.length;
        }

        // El mouse: se convierte la posición de la ventana a posición del mundo del juego
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        if (mouse.x != ultimoMouseX || mouse.y != ultimoMouseY)
        {
            ultimoMouseX = mouse.x;
            ultimoMouseY = mouse.y;
            for (int i = 0; i < areas.length; i++)
            {
                if (areas[i].contains(mouse))
                {
                    seleccion = i;
                }
            }
        }
        if (Gdx.input.justTouched())
        {
            for (int i = 0; i < areas.length; i++)
            {
                if (areas[i].contains(mouse))
                {
                    seleccion = i;
                    GestorRecursos.getInstance().reproducirSonido("ui_aceptar.wav");
                    return i;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
        {
            GestorRecursos.getInstance().reproducirSonido("ui_aceptar.wav");
            return seleccion;
        }
        if (seleccion != seleccionAntes)
        {
            GestorRecursos.getInstance().reproducirSonido("ui_mover.wav");
        }
        return -1;
    }

    public void dibujar(SpriteBatch batch, BitmapFont font)
    {
        Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
        font.getData().setScale(1.5f);
        for (int i = 0; i < areas.length; i++)
        {
            Rectangle r = areas[i];
            boolean activo = i == seleccion;

            // borde (amarillo si es el elegido) y fondo
            if (activo)
            {
                batch.setColor(1f, 0.85f, 0.2f, 1f);
            }
            else
            {
                batch.setColor(0.35f, 0.4f, 0.7f, 1f);
            }
            batch.draw(pixel, r.x - 3, r.y - 3, r.width + 6, r.height + 6);
            if (activo)
            {
                batch.setColor(0.2f, 0.2f, 0.5f, 1f);
            }
            else
            {
                batch.setColor(0.06f, 0.06f, 0.18f, 1f);
            }
            batch.draw(pixel, r.x, r.y, r.width, r.height);
            batch.setColor(Color.WHITE);

            // texto centrado (la fuente se dibuja hacia abajo desde su borde superior)
            if (activo)
            {
                font.setColor(1f, 0.85f, 0.2f, 1f);
            }
            float centroY = r.y + r.height / 2;
            font.draw(batch, etiquetas[i], r.x, centroY + 11 * font.getData().scaleY, r.width, Align.center, false);
            font.setColor(Color.WHITE);
        }
    }

    public void setEtiqueta(int i, String texto) { etiquetas[i] = texto; }
    public int getSeleccion() { return seleccion; }
}
