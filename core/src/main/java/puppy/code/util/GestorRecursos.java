package puppy.code.util;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.GdxRuntimeException;

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
    private HashMap<String, Music> musicas;
    private String pistaActual;
    private Texture pixelBlanco;
    private BitmapFont fuente;
    private Opciones opciones;

    // Récord de puntaje: se guarda en un archivo de texto para que no se pierda al cerrar el juego
    private static final String ARCHIVO_RECORD = "record.txt";
    private int record;

    // Constructor privado: así nadie más puede crear otra instancia
    private GestorRecursos()
    {
        texturas = new HashMap<>();
        sonidos = new HashMap<>();
        musicas = new HashMap<>();
        record = leerRecord();
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

    // ---------------- Récord (persistencia en archivo + manejo de excepciones) ----------------

    public int getRecord()
    {
        return record;
    }

    /*
        Se llama al terminar una partida. Si el puntaje supera el récord, lo guarda
        en el archivo y devuelve true (para avisarle al jugador).
    */
    public boolean registrarPuntaje(int puntaje)
    {
        if (puntaje > record)
        {
            record = puntaje;
            guardarRecord();
            return true;
        }
        return false;
    }

    /*
        Lee el récord del archivo. Si el archivo no existe, está dañado o no se puede
        leer, NO se cae el juego: se atrapa la excepción con try-catch y se empieza en 0.
    */
    private int leerRecord()
    {
        try
        {
            FileHandle archivo = Gdx.files.local(ARCHIVO_RECORD);
            if (!archivo.exists())
            {
                return 0;
            }
            return Integer.parseInt(archivo.readString().trim());
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
    private void guardarRecord()
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
