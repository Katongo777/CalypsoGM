package puppy.code.modelo;

/*
    Los niveles de dificultad. Es un enum: cada valor guarda sus propios datos y la
    pantalla de juego solo le pregunta "¿cuánto multiplico la velocidad?", sin
    tener que saber qué dificultad es.
*/
public enum Dificultad
{
    FACIL("Fácil", 0.75f, 2.0f, 3, 0.70f, 1.5f),
    NORMAL("Normal", 0.9f, 1.4f, 1, 0.85f, 1.25f),
    DIFICIL("Difícil", 1.0f, 1.0f, 0, 1.0f, 1.0f);

    private final String nombre;
    private final float multVelocidad;   // multiplica la velocidad de los enemigos
    private final float multIntervalo;   // multiplica el tiempo entre disparos enemigos (más grande = disparan menos)
    private final int vidasExtra;        // vidas que se suman a las del personaje
    private final float multBalas;       // multiplica la velocidad de las balas enemigas (menos = más lentas)
    private final float multAviso;       // multiplica el tiempo de aviso de los rayos (más = más tiempo para esquivar)

    Dificultad(String nombre, float multVelocidad, float multIntervalo, int vidasExtra, float multBalas, float multAviso)
    {
        this.nombre = nombre;
        this.multVelocidad = multVelocidad;
        this.multIntervalo = multIntervalo;
        this.vidasExtra = vidasExtra;
        this.multBalas = multBalas;
        this.multAviso = multAviso;
    }

    // La siguiente dificultad de la lista (después de la última vuelve a la primera)
    public Dificultad siguiente(int paso)
    {
        Dificultad[] todas = values();
        return todas[(ordinal() + paso + todas.length) % todas.length];
    }

    public String getNombre() { return nombre; }
    public float getMultVelocidad() { return multVelocidad; }
    public float getMultIntervalo() { return multIntervalo; }
    public int getVidasExtra() { return vidasExtra; }
    public float getMultBalas() { return multBalas; }
    public float getMultAviso() { return multAviso; }
}
