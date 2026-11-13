package puppy.code.modelo.niveles;

import java.util.Random;

import puppy.code.modelo.Dificultad;
import puppy.code.modelo.enemigos.EnemigoBot;
import puppy.code.modelo.enemigos.EnemigoCheater;
import puppy.code.modelo.enemigos.EnemigoJefe;
import puppy.code.modelo.enemigos.EnemigoLag;
import puppy.code.modelo.enemigos.EnemigoSniper;
import puppy.code.modelo.enemigos.EnemigoSpammer;
import puppy.code.modelo.enemigos.TipoJefe;
import puppy.code.util.GestorRecursos;

/*
    PATRÓN BUILDER (GM-9): este es el CONSTRUCTOR CONCRETO.

    Implementa los pasos de NivelBuilder y arma por dentro el Nivel. Es el único lugar
    del juego que crea a los enemigos de una oleada: cada paso "agregar" anota cuántos
    enemigos de ese tipo van, y getResult() termina el producto creándolos con la
    velocidad de la oleada y de la dificultad elegida. Así PantallaJuego recibe la
    oleada lista y no necesita conocer las clases concretas de los enemigos.
*/
public class NivelPartidaBuilder implements NivelBuilder
{
    private static final float VELOCIDAD_MAXIMA = 4.5f;

    private final Dificultad dificultad;
    private final Random azar = new Random();
    private Nivel nivel;
    private float velocidad;
    private int bots, lags, cheaters, spammers, snipers;
    private TipoJefe tipoJefe;

    public NivelPartidaBuilder(Dificultad dificultad)
    {
        this.dificultad = dificultad;
        reset();
    }

    // Empieza un nivel nuevo, con valores por defecto: sin enemigos, sin jefe y velocidad normal
    @Override
    public void reset()
    {
        nivel = new Nivel();
        velocidad = 1f;
        bots = 0;
        lags = 0;
        cheaters = 0;
        spammers = 0;
        snipers = 0;
        tipoJefe = null;
    }

    @Override
    public void definirNumero(int numero)
    {
        nivel.definirNumero(numero);
    }

    @Override
    public void definirNombre(String nombre)
    {
        nivel.definirNombre(nombre);
    }

    @Override
    public void agregarBots(int cantidad)
    {
        bots += Math.max(0, cantidad);
    }

    @Override
    public void agregarLags(int cantidad)
    {
        lags += Math.max(0, cantidad);
    }

    @Override
    public void agregarCheaters(int cantidad)
    {
        cheaters += Math.max(0, cantidad);
    }

    @Override
    public void agregarSpammers(int cantidad)
    {
        spammers += Math.max(0, cantidad);
    }

    @Override
    public void agregarSnipers(int cantidad)
    {
        snipers += Math.max(0, cantidad);
    }

    @Override
    public void agregarJefe(TipoJefe tipo)
    {
        tipoJefe = tipo;
    }

    @Override
    public void definirAviso(String aviso)
    {
        nivel.definirAviso(aviso);
    }

    @Override
    public void definirVelocidad(float velocidad)
    {
        this.velocidad = velocidad;
    }

    @Override
    public void definirProbPowerUp(float probabilidad)
    {
        nivel.definirProbPowerUp(probabilidad);
    }

    @Override
    public void definirAgresividad(float agresividad)
    {
        nivel.definirAgresividad(agresividad);
    }

    /*
        Último paso: crea los enemigos anotados y entrega el Nivel terminado.
        Después de entregarlo empieza uno nuevo, para que el mismo Builder se pueda
        volver a usar sin mezclar oleadas.
    */
    @Override
    public Nivel getResult()
    {
        GestorRecursos recursos = GestorRecursos.getInstance();
        for (int i = 0; i < bots; i++)
        {
            nivel.agregarEnemigo(new EnemigoBot(0, 0, velocidadAlAzar(1f), velocidadAlAzar(1f), recursos.getTextura("bot.png")));
        }
        for (int i = 0; i < lags; i++)
        {
            nivel.agregarEnemigo(new EnemigoLag(0, 0, velocidadAlAzar(1.3f), velocidadAlAzar(1.3f), recursos.getTextura("lag.png")));
        }
        for (int i = 0; i < cheaters; i++)
        {
            nivel.agregarEnemigo(new EnemigoCheater(0, 0, velocidadAlAzar(0.5f), velocidadAlAzar(0.5f), recursos.getTextura("cheater.png")));
        }
        for (int i = 0; i < spammers; i++)
        {
            // el Spammer se mueve solo de lado
            nivel.agregarEnemigo(new EnemigoSpammer(0, 0, velocidadAlAzar(1.1f), 0, recursos.getTextura("spammer.png")));
        }
        for (int i = 0; i < snipers; i++)
        {
            nivel.agregarEnemigo(new EnemigoSniper(0, 0, velocidadAlAzar(0.5f), velocidadAlAzar(0.5f), recursos.getTextura("sniper.png")));
        }
        nivel.mezclarEnemigos(azar);
        if (tipoJefe != null)
        {
            nivel.definirJefe(new EnemigoJefe(tipoJefe, recursos.getTextura(tipoJefe.getArchivoImagen())));
        }
        Nivel terminado = nivel;
        reset();
        return terminado;
    }

    // Velocidad al azar (entre 1,2 y 2,4), escalada por la oleada y la dificultad, con dirección al azar.
    // El factor es propio de cada tipo de enemigo: el Lag es más rápido y el Cheater más lento
    private float velocidadAlAzar(float factor)
    {
        float v = Math.min((1.2f + azar.nextFloat() * 1.2f) * velocidad * dificultad.getMultVelocidad(), VELOCIDAD_MAXIMA) * factor;
        return azar.nextBoolean() ? v : -v;
    }
}
