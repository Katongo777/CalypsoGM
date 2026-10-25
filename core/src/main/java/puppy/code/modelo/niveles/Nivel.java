package puppy.code.modelo.niveles;

/*
    PATRÓN BUILDER (GM-9): este es el PRODUCTO.

    Un Nivel (o "oleada") describe qué enemigos aparecen y qué tan difícil es:
    cuántos bots, lags y cheaters, la velocidad y la probabilidad de que dejen
    un power-up.

    Es un objeto con muchas partes opcionales. Si lo crearamos con un constructor
    tendría que recibir 7 parámetros seguidos (new Nivel(1, "x", 5, 0, 0, 1f, 0.25f)),
    algo difícil de leer y fácil de equivocar. Por eso lo arma un Builder, paso a paso.
*/
public class Nivel
{
    private int numero;
    private String nombre;
    private int cantBots;
    private int cantLags;
    private int cantCheaters;
    private float velocidad;
    private float probPowerUp;

    // ---- Getters y setters ----
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantBots() { return cantBots; }
    public void setCantBots(int cantBots) { this.cantBots = cantBots; }

    public int getCantLags() { return cantLags; }
    public void setCantLags(int cantLags) { this.cantLags = cantLags; }

    public int getCantCheaters() { return cantCheaters; }
    public void setCantCheaters(int cantCheaters) { this.cantCheaters = cantCheaters; }

    public float getVelocidad() { return velocidad; }
    public void setVelocidad(float velocidad) { this.velocidad = velocidad; }

    public float getProbPowerUp() { return probPowerUp; }
    public void setProbPowerUp(float probPowerUp) { this.probPowerUp = probPowerUp; }

    public int getTotalEnemigos()
    {
        return cantBots + cantLags + cantCheaters;
    }
}
