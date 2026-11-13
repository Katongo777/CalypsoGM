package puppy.code.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/*
    PATRON SINGLETON (GM-6)

    Problema: cada vez que se creaba una pantalla de juego (una por ronda) se volvian
    a cargar las mismas imagenes y sonidos desde el disco, y nunca se liberaban:
    el programa gastaba memoria de mas (fuga de memoria).

    Solucion: esta clase tiene UNA sola instancia en todo el programa. Guarda cada
    recurso en un HashMap la primera vez que se pide, y despues lo reutiliza.
    Solo se ocupa de los recursos (y de reproducirlos con el volumen elegido); el récord
    de puntaje vive en su propia clase, GestorRecord.

    Como se logra que sea unica:
      1. El constructor es privado: nadie puede hacer "new GestorRecursos()".
      2. La instancia se guarda en un atributo estático.
      3. getInstance() la crea la primera vez y despues siempre devuelve la misma.
*/
public class GestorRecursos
{
    // La única instancia de la clase (atributo estático)
    private static GestorRecursos instancia;

    // Se declaran como Map (la interfaz) y no como HashMap: el resto de la clase no depende de la implementación
    private final Map<String, Texture> texturas;
    private final Map<String, Sound> sonidos;
    private final Map<String, Music> musicas;
    private String pistaActual;
    private Texture pixelBlanco;
    private BitmapFont fuente;
    private Opciones opciones;

    // Constructor privado: así nadie más puede crear otra instancia
    private GestorRecursos()
    {
        texturas = new HashMap<>();
        sonidos = new HashMap<>();
        musicas = new HashMap<>();
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

    /*
        Una textura de 1x1 píxel blanco, creada por código. Sirve para dibujar
        rectángulos de cualquier tamaño y color (estrellas, sombras, etc.).
    */
    public Texture getPixelBlanco()
    {
        if (pixelBlanco == null)
        {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            pixelBlanco = new Texture(pm);
            pm.dispose();
        }
        return pixelBlanco;
    }

    /*
        La música: hay varias canciones (menú, juego y jefe) pero suena UNA sola a la vez.
        Si se pide la que ya está sonando no se reinicia, así la música no se corta al
        cambiar de pantalla.
    */
    public void reproducirMusica(String archivo)
    {
        if (archivo.equals(pistaActual) && musicas.get(archivo).isPlaying())
        {
            return;
        }
        detenerMusica();
        Music m = musicas.get(archivo);
        if (m == null)
        {
            m = Gdx.audio.newMusic(Gdx.files.internal(archivo));
            m.setLooping(true);
            musicas.put(archivo, m);
        }
        m.setVolume(getOpciones().getVolumenMusica());
        m.play();
        pistaActual = archivo;
    }

    public void detenerMusica()
    {
        if (pistaActual != null && musicas.get(pistaActual) != null)
        {
            musicas.get(pistaActual).stop();
        }
        pistaActual = null;
    }

    // Las opciones del jugador (volumen y dificultad), guardadas en un archivo
    public Opciones getOpciones()
    {
        if (opciones == null)
        {
            opciones = new Opciones();
        }
        return opciones;
    }

    // Reproduce un efecto de sonido con el volumen de efectos elegido en las opciones
    public void reproducirSonido(String archivo)
    {
        getSonido(archivo).play(getOpciones().getVolumenEfectos());
    }

    // Igual, pero con volumen y tono propios (se multiplica por el volumen de efectos de las opciones)
    public void reproducirSonido(String archivo, float volumen, float tono)
    {
        getSonido(archivo).play(volumen * getOpciones().getVolumenEfectos(), tono, 0);
    }

    // Aplica a la música el volumen elegido en las opciones
    public void aplicarVolumenMusica()
    {
        for (Music m : musicas.values())
        {
            m.setVolume(getOpciones().getVolumenMusica());
        }
    }

    // Fuente pixel-art propia (assets/pixel.fnt). Se carga la primera vez que se pide
    public BitmapFont getFuente()
    {
        if (fuente == null)
        {
            fuente = new BitmapFont(Gdx.files.internal("pixel.fnt"));
            // Nearest para que los pixeles no se vean borrosos al escalar
            fuente.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        return fuente;
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
        if (pixelBlanco != null)
        {
            pixelBlanco.dispose();
            pixelBlanco = null;
        }
        if (fuente != null)
        {
            fuente.dispose();
            fuente = null;
        }
        texturas.clear();
        sonidos.clear();
        for (Music m : musicas.values())
        {
            m.dispose();
        }
        musicas.clear();
        pistaActual = null;
    }
}
