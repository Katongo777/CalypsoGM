package puppy.code.modelo.niveles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import puppy.code.modelo.enemigos.Enemigo;

/*
    PATRÓN BUILDER (GM-9): este es el PRODUCTO.

    Un Nivel (u "oleada") trae todo lo que la partida necesita para jugarla: su número y
    nombre, el aviso que se muestra al empezar, la probabilidad de que caigan power-ups,
    los enemigos ya creados (listos para entrar a la pantalla) y, si corresponde, su jefe.

    Tiene muchas partes opcionales y los enemigos dependen de la oleada y de la dificultad.
    Por eso no se arma con un constructor gigante ni desde afuera: lo arma un Builder,
    paso a paso. Los métodos que lo modifican NO son públicos (visibilidad de paquete):
    solo el Builder, que está en este mismo paquete, puede usarlos. Para el resto del
    juego un Nivel terminado es de solo lectura.
*/
public class Nivel
{
    private int numero = 1;
    private String nombre = "Oleada";
    private String aviso = "";           // texto que se muestra en la intro (por ejemplo, un enemigo nuevo)
    private float probPowerUp = 0f;
    private float agresividad = 1f;      // 1 = normal; más = los enemigos disparan más seguido y más rápido
    private Enemigo jefe;                // null si la oleada no tiene jefe
    private final List<Enemigo> enemigos = new ArrayList<>();

    // El constructor tampoco es público: un Nivel solo nace dentro de un Builder
    Nivel()
    {
    }

    // ---- Lo usa solo el Builder (visibilidad de paquete) ----
    void definirNumero(int numero)
    {
        this.numero = Math.max(1, numero);
    }

    void definirNombre(String nombre)
    {
        this.nombre = nombre;
    }

    void definirAviso(String aviso)
    {
        this.aviso = aviso;
    }

    void definirProbPowerUp(float probabilidad)
    {
        this.probPowerUp = Math.max(0f, Math.min(probabilidad, 1f));
    }

    void definirAgresividad(float agresividad)
    {
        this.agresividad = Math.max(0.5f, Math.min(agresividad, 2f));
    }

    void definirJefe(Enemigo jefe)
    {
        this.jefe = jefe;
    }

    void agregarEnemigo(Enemigo enemigo)
    {
        enemigos.add(enemigo);
    }

    // Mezcla el orden de entrada, para que no salgan todos los bots primero y después los demás
    void mezclarEnemigos(Random azar)
    {
        Collections.shuffle(enemigos, azar);
    }

    // ---- Lo que puede consultar el resto del juego ----
    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public String getAviso() { return aviso; }
    public float getProbPowerUp() { return probPowerUp; }
    public float getAgresividad() { return agresividad; }

    public boolean tieneJefe()
    {
        return jefe != null;
    }

    public Enemigo getJefe()
    {
        return jefe;
    }

    // Lista de solo lectura: nadie de afuera puede agregar ni quitar enemigos del nivel
    public List<Enemigo> getEnemigos()
    {
        return Collections.unmodifiableList(enemigos);
    }

    public int getTotalEnemigos()
    {
        return enemigos.size() + (tieneJefe() ? 1 : 0);
    }
}
