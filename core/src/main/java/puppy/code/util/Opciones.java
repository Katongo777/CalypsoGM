package puppy.code.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import puppy.code.modelo.Dificultad;

/*
    Las opciones del jugador: volumen de la música, volumen de los efectos y dificultad.
    Se guardan en el archivo "opciones.txt" para que no se pierdan al cerrar el juego.

    Igual que con el récord, si el archivo no existe o está dañado NO se cae el juego:
    se atrapan las excepciones con try-catch y se usan los valores por defecto.
*/
public class Opciones
{
    private static final String ARCHIVO = "opciones.txt";

    private float volumenMusica = 0.6f;
    private float volumenEfectos = 0.7f;
    private Dificultad dificultad = Dificultad.NORMAL;

    public Opciones()
    {
        cargar();
    }

    // El archivo tiene una sola línea, por ejemplo: 0.6;0.7;NORMAL
    private void cargar()
    {
        try
        {
            FileHandle archivo = Gdx.files.local(ARCHIVO);
            if (!archivo.exists())
            {
                return;
            }
            String[] partes = archivo.readString().trim().split(";");
            volumenMusica = limitar(Float.parseFloat(partes[0]));
            volumenEfectos = limitar(Float.parseFloat(partes[1]));
            dificultad = Dificultad.valueOf(partes[2]);
        }
        catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) // incluye NumberFormatException
        {
            System.out.println("El archivo de opciones es inválido, se usan los valores por defecto.");
        }
        catch (GdxRuntimeException e)
        {
            System.out.println("No se pudieron leer las opciones: " + e.getMessage());
        }
    }

    public void guardar()
    {
        try
        {
            Gdx.files.local(ARCHIVO).writeString(volumenMusica + ";" + volumenEfectos + ";" + dificultad.name(), false);
        }
        catch (GdxRuntimeException e)
        {
            System.out.println("No se pudieron guardar las opciones: " + e.getMessage());
        }
    }

    // Deja un volumen entre 0 y 1
    private float limitar(float v)
    {
        return Math.max(0f, Math.min(1f, v));
    }

    public float getVolumenMusica() { return volumenMusica; }
    public void setVolumenMusica(float v) { volumenMusica = limitar(v); }

    public float getVolumenEfectos() { return volumenEfectos; }
    public void setVolumenEfectos(float v) { volumenEfectos = limitar(v); }

    public Dificultad getDificultad() { return dificultad; }
    public void setDificultad(Dificultad dificultad) { this.dificultad = dificultad; }
}
