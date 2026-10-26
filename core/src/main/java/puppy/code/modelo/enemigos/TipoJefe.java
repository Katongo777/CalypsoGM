package puppy.code.modelo.enemigos;

/*
    Los jefes que existen en el juego. Es un enum: cada valor guarda sus propios datos
    (nombre, imagen, vida, puntos y velocidad), así el Nivel solo tiene que decir
    "esta oleada trae al TOXIC" y EnemigoJefe sabe cómo armarlo.
*/
public enum TipoJefe
{
    TOXIC("EL TOXIC", "jefe_toxic.png", 45, 500, 2f),
    HACKER("EL HACKER", "jefe_hacker.png", 70, 1000, 2.2f),
    SWEATY("EL SWEATY", "jefe_sweaty.png", 100, 1500, 2.6f),
    ADMIN("EL ADMIN", "jefe_admin.png", 150, 3000, 2.4f);

    private final String nombre;
    private final String archivoImagen;
    private final int vida;
    private final int puntos;
    private final float velocidad;

    TipoJefe(String nombre, String archivoImagen, int vida, int puntos, float velocidad)
    {
        this.nombre = nombre;
        this.archivoImagen = archivoImagen;
        this.vida = vida;
        this.puntos = puntos;
        this.velocidad = velocidad;
    }

    public String getNombre() { return nombre; }
    public String getArchivoImagen() { return archivoImagen; }
    public int getVida() { return vida; }
    public int getPuntos() { return puntos; }
    public float getVelocidad() { return velocidad; }
}
