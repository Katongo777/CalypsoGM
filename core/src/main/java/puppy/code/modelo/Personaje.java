package puppy.code.modelo;

import puppy.code.modelo.armas.Escopeta;
import puppy.code.modelo.armas.EstrategiaDisparo;
import puppy.code.modelo.armas.Pistola;
import puppy.code.modelo.armas.Railgun;

/*
    Los personajes jugables ("clases" de la selección de personaje).

    Es un enum: una lista fija de valores, y cada valor guarda sus propios datos.
    Cada personaje define con qué nave, cuántas vidas, qué velocidad y qué arma
    (una EstrategiaDisparo) empieza la partida.
*/
public enum Personaje
{
    TRYHARD("Tryhard", "nave_tryhard.png", 3, 7f, "Suda hasta en el tutorial."),
    CAMPER("Camper", "nave_camper.png", 2, 5f, "Una bala, una baja."),
    SUPPORT("Main Support", "nave_support.png", 4, 6f, "Cura con amor... y con perdigones.");

    private final String nombre;
    private final String archivoNave;
    private final int vidas;
    private final float velocidad;
    private final String frase;

    Personaje(String nombre, String archivoNave, int vidas, float velocidad, String frase)
    {
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
        switch (this)
        {
            case CAMPER:
                return new Railgun();
            case SUPPORT:
                return new Escopeta();
            default:
                return new Pistola();
        }
    }

    public String getNombre() { return nombre; }
    public String getArchivoNave() { return archivoNave; }
    public int getVidas() { return vidas; }
    public float getVelocidad() { return velocidad; }
    public String getFrase() { return frase; }
}
