package puppy.code.modelo.niveles;

import puppy.code.modelo.enemigos.TipoJefe;

/*
    PATRÓN BUILDER (GM-9): este es el PRODUCTO.

    Un Nivel (o "oleada") describe qué enemigos aparecen y qué tan difícil es:
    cuántos bots, lags, cheaters, spammers y snipers, si trae un jefe, un aviso para el jugador,
    la velocidad y la probabilidad de que dejen un power-up.

    Es un objeto con muchas partes opcionales. Si lo crearamos con un constructor
    tendría que recibir 9 parámetros seguidos (new Nivel(1, "x", 5, 0, 0, null, "", 1f, 0.25f)),
    algo difícil de leer y fácil de equivocar. Por eso lo arma un Builder, paso a paso.
*/
public class Nivel
{
    private int numero;
    private String nombre;
    private int cantBots;
    private int cantLags;
    private int cantCheaters;
    private int cantSpammers;
    private int cantSnipers;
    private float velocidad;
    private float probPowerUp;
    private TipoJefe tipoJefe;  // null si la oleada no tiene jefe
    private String aviso;       // texto que se muestra en la intro (por ejemplo, un enemigo nuevo)

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

    public int getCantSpammers() { return cantSpammers; }
    public void setCantSpammers(int cantSpammers) { this.cantSpammers = cantSpammers; }

    public int getCantSnipers() { return cantSnipers; }
    public void setCantSnipers(int cantSnipers) { this.cantSnipers = cantSnipers; }

    public float getVelocidad() { return velocidad; }
    public void setVelocidad(float velocidad) { this.velocidad = velocidad; }

    public float getProbPowerUp() { return probPowerUp; }
    public void setProbPowerUp(float probPowerUp) { this.probPowerUp = probPowerUp; }

    public TipoJefe getTipoJefe() { return tipoJefe; }
    public void setTipoJefe(TipoJefe tipoJefe) { this.tipoJefe = tipoJefe; }

    public String getAviso() { return aviso; }
    public void setAviso(String aviso) { this.aviso = aviso; }

    public boolean tieneJefe()
    {
        return tipoJefe != null;
    }

    public int getTotalEnemigos()
    {
        return cantBots + cantLags + cantCheaters + cantSpammers + cantSnipers + (tieneJefe() ? 1 : 0);
    }
}
