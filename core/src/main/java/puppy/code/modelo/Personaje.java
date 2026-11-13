package puppy.code.modelo;

import java.util.function.Supplier;

import puppy.code.modelo.armas.Escopeta;
import puppy.code.modelo.armas.EstrategiaDisparo;
import puppy.code.modelo.armas.Pistola;
import puppy.code.modelo.armas.Railgun;
import puppy.code.modelo.habilidades.Burbuja;
import puppy.code.modelo.habilidades.Habilidad;
import puppy.code.modelo.habilidades.Overdrive;
import puppy.code.modelo.habilidades.TiroCertero;

/*
    Los personajes jugables ("clases" de la selección de personaje).

    Es un enum: una lista fija de valores, y cada valor guarda sus propios datos.
    Cada personaje define con qué nave, cuántas vidas, qué velocidad y qué arma
    (una EstrategiaDisparo) empieza la partida.
*/
public enum Personaje
{
    TRYHARD("Tryhard", "nave_tryhard.png", 4, 7f, "Suda hasta en el tutorial.", Pistola::new, Overdrive::new),
    CAMPER("Camper", "nave_camper.png", 5, 6f, "Una bala, una baja.", Railgun::new, TiroCertero::new),
    SUPPORT("Main Support", "nave_support.png", 5, 6f, "Cura con amor... y con perdigones.", Escopeta::new, Burbuja::new);

    private final String nombre;
    private final String archivoNave;
    private final int vidas;
    private final float velocidad;
    private final String frase;
    // Cómo se crea su arma y su habilidad: un personaje nuevo solo agrega una línea arriba (Abierto/Cerrado)
    private final Supplier<EstrategiaDisparo> fabricaArma;
    private final Supplier<Habilidad> fabricaHabilidad;

    Personaje(String nombre, String archivoNave, int vidas, float velocidad, String frase,
              Supplier<EstrategiaDisparo> fabricaArma, Supplier<Habilidad> fabricaHabilidad)
    {
        this.fabricaArma = fabricaArma;
        this.fabricaHabilidad = fabricaHabilidad;
        this.nombre = nombre;
        this.archivoNave = archivoNave;
        this.vidas = vidas;
        this.velocidad = velocidad;
        this.frase = frase;
    }

    /*
        Crea el arma inicial del personaje. Cada personaje devuelve una estrategia
        distinta, pero quien la recibe solo ve una EstrategiaDisparo (Strategy).
    */
    public EstrategiaDisparo crearArma()
    {
        return fabricaArma.get();
    }

    /*
        Crea la habilidad especial del personaje (se activa con la tecla X cuando la barra
        de energía está llena). Igual que el arma, quien la recibe solo ve una Habilidad.
    */
    public Habilidad crearHabilidad()
    {
        return fabricaHabilidad.get();
    }

    public String getNombre() { return nombre; }
    public String getArchivoNave() { return archivoNave; }
    public int getVidas() { return vidas; }
    public float getVelocidad() { return velocidad; }
    public String getFrase() { return frase; }
}
