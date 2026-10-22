package puppy.code.util;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

/*
    PATRON SINGLETON (GM-6)

    Problema: cada vez que se creaba una pantalla de juego (una por ronda) se volvian
    a cargar las mismas imagenes y sonidos desde el disco, y nunca se liberaban:
    el programa gastaba memoria de mas (fuga de memoria).

    Solucion: esta clase tiene UNA sola instancia en todo el programa. Guarda cada
    recurso en un HashMap la primera vez que se pide, y despues lo reutiliza.

    Como se logra que sea unica:
      1. El constructor es privado: nadie puede hacer "new GestorRecursos()".
      2. La instancia se guarda en un atributo estático.
      3. getInstance() la crea la primera vez y despues siempre devuelve la misma.
*/
public class GestorRecursos
{
    // La única instancia de la clase (atributo estático)
    private static GestorRecursos instancia;

    private HashMap<String, Texture> texturas;
    private HashMap<String, Sound> sonidos;
    private Music musica;

    // Constructor privado: así nadie más puede crear otra instancia
    private GestorRecursos()
    {
        texturas = new HashMap<>();
        sonidos = new HashMap<>();
    }

    // Punto de acceso global: crea la instancia solo la primera vez
    public static GestorRecursos getInstance()
    {
        if (instancia == null)
        {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    // Devuelve la textura; si no estaba cargada, la carga y la guarda
    public Texture getTextura(String archivo)
    {
        Texture tx = texturas.get(archivo);
        if (tx == null)
        {
            tx = new Texture(Gdx.files.internal(archivo));
            // Nearest = sin suavizado, así el pixel-art se ve nítido
            tx.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            texturas.put(archivo, tx);
        }
        return tx;
    }

    // Igual que getTextura, pero para efectos de sonido
    public Sound getSonido(String archivo)
    {
        Sound s = sonidos.get(archivo);
        if (s == null)
        {
            s = Gdx.audio.newSound(Gdx.files.internal(archivo));
            sonidos.put(archivo, s);
        }
        return s;
    }

    // La música de fondo también es única: así no se reinicia en cada ronda
    public Music getMusica()
    {
        if (musica == null)
        {
            musica = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
            musica.setLooping(true);
            musica.setVolume(0.5f);
        }
        return musica;
    }

    // Se llama una sola vez, al cerrar el juego, para liberar todo
    public void liberar()
    {
        for (Texture tx : texturas.values())
        {
            tx.dispose();
        }
        for (Sound s : sonidos.values())
        {
            s.dispose();
        }
        texturas.clear();
        sonidos.clear();
        if (musica != null)
        {
            musica.dispose();
            musica = null;
        }
    }
}
