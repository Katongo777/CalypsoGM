package puppy.code.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/*
    El récord de puntaje: lo recuerda y lo guarda en un archivo de texto para que no se
    pierda al cerrar el juego.

    Antes esto vivía dentro de GestorRecursos, pero ese Singleton debe ocuparse solo de
    cargar y compartir recursos (imágenes, sonidos, música). Guardar puntajes es otra
    responsabilidad, así que tiene su propia clase (principio de responsabilidad única).
    No es un Singleton: el juego (SpaceNavigation) crea uno solo y lo presta a las pantallas.
*/
public class GestorRecord
{
    private static final String ARCHIVO_RECORD = "record.txt";
    private int record;

    public GestorRecord()
    {
        record = leer();
    }

    public int getRecord()
    {
        return record;
    }

    /*
        Se llama al terminar una partida. Si el puntaje supera el récord, lo guarda
        en el archivo y devuelve true (para avisarle al jugador). Un puntaje negativo
        nunca es válido.
    */
    public boolean registrar(int puntaje)
    {
        if (puntaje > record && puntaje >= 0)
        {
            record = puntaje;
            guardar();
            return true;
        }
        return false;
    }

    /*
        Lee el récord del archivo. Si el archivo no existe, está dañado o no se puede
        leer, NO se cae el juego: se atrapa la excepción con try-catch y se empieza en 0.
    */
    private int leer()
    {
        try
        {
            FileHandle archivo = Gdx.files.local(ARCHIVO_RECORD);
            if (!archivo.exists())
            {
                return 0;
            }
            return Math.max(0, Integer.parseInt(archivo.readString().trim()));
        }
        catch (NumberFormatException e)
        {
            System.out.println("El archivo del récord tiene un valor inválido, se empieza desde 0.");
            return 0;
        }
        catch (GdxRuntimeException e)
        {
            System.out.println("No se pudo leer el récord: " + e.getMessage());
            return 0;
        }
    }

    // Escribe el récord en el archivo; si falla, avisa por consola pero el juego sigue
    private void guardar()
    {
        try
        {
            Gdx.files.local(ARCHIVO_RECORD).writeString(String.valueOf(record), false);
        }
        catch (GdxRuntimeException e)
        {
            System.out.println("No se pudo guardar el récord: " + e.getMessage());
        }
    }
}
