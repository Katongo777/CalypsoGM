package puppy.code.modelo;

/*
    Los niveles de dificultad. Es un enum: cada valor guarda sus propios datos y la
    pantalla de juego solo le pregunta "¿cuánto multiplico la velocidad?", sin
    tener que saber qué dificultad es.
*/
public enum Dificultad
{
    FACIL("Fácil", 0.85f, 1.4f, 1),
    NORMAL("Normal", 1f, 1f, 0),
    DIFICIL("Difícil", 1.15f, 0.75f, 0);

    private final String nombre;
    private final float multVelocidad;   // multiplica la velocidad de los enemigos
    private final float multIntervalo;   // multiplica el tiempo entre disparos enemigos (más grande = disparan menos)
    private final int vidasExtra;        // vidas que se suman a las del personaje

    Dificultad(String nombre, float multVelocidad, float multIntervalo, int vidasExtra)
    {
        this.nombre = nombre;
        this.multVelocidad = multVelocidad;
        this.multIntervalo = multIntervalo;
        this.vidasExtra = vidasExtra;
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
}
