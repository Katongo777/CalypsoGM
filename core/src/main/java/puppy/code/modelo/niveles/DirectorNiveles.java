package puppy.code.modelo.niveles;

import puppy.code.modelo.enemigos.TipoJefe;

/*
    PATRÓN BUILDER (GM-9): este es el DIRECTOR.

    Conoce las "recetas": en qué orden y con qué valores hay que llamar al Builder
    para obtener cada tipo de oleada. El mismo proceso de construcción (el Builder)
    sirve para armar oleadas muy distintas.

    La campaña tiene 20 oleadas, en 4 bloques de 5. Al final de cada bloque hay un jefe
    y cada bloque presenta algo nuevo:
        1 a 4    Bots, Lags y Cheaters (oleada 4: hardcore)
        5        JEFE: El Toxic
        6 a 9    Llegan los Spammers (oleada 8: hardcore)
        10       JEFE
        11 a 14  Llegan los Snipers (oleada 12: hardcore)
        15       JEFE
        16 a 19  Todo junto (oleadas 17 y 19: hardcore)
        20       JEFE FINAL: El Admin
*/
public class DirectorNiveles
{
    public static final int TOTAL_OLEADAS = 20;

    // Nombre de cada oleada (el índice 0 es la oleada 1)
    private static final String[] NOMBRES = {
        "Bots en el lobby",
        "Se cae el wifi",
        "Hay cheaters en la partida",
        "¡MODO HARDCORE!",
        "JEFE: EL TOXIC",
        "Llegan los spammers",
        "Partida clasificatoria",
        "¡HARDCORE: ELO HELL!",
        "Todos son tryhards",
        "JEFE: EL HACKER",
        "Aparecen los snipers",
        "¡HARDCORE: TORNEO!",
        "Quedan los mejores",
        "Lag spike masivo",
        "JEFE: EL SWEATY",
        "La calma antes de la tormenta",
        "¡HARDCORE: GRAN FINAL!",
        "Servidor en llamas",
        "¡HARDCORE: ÚLTIMA RONDA!",
        "JEFE FINAL: EL ADMIN"
    };

    private NivelBuilder builder;

    public DirectorNiveles(NivelBuilder builder)
    {
        this.builder = builder;
    }

    private String nombreDe(int numero)
    {
        if (numero >= 1 && numero <= NOMBRES.length)
        {
            return NOMBRES[numero - 1];
        }
        return "Oleada " + numero;
    }

    // La velocidad crece de a poco y tiene un tope, para que nunca sea imposible
    private float velocidadDe(int numero)
    {
        return Math.min(0.9f + 0.025f * numero, 1.35f);
    }

    // Receta 1: oleada normal. Va creciendo de a poco con el número de oleada
    public Nivel construirOleadaNormal(int numero)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre(nombreDe(numero));
        builder.agregarBots(Math.min(3 + numero / 3, 7));
        if (numero >= 2)
        {
            builder.agregarLags(Math.min(1 + numero / 5, 4));
        }
        if (numero >= 3)
        {
            builder.agregarCheaters(Math.min(1 + (numero - 3) / 5, 3));
        }
        if (numero >= 6)
        {
            builder.agregarSpammers(Math.min(1 + (numero - 6) / 4, 3));
        }
        if (numero >= 11)
        {
            builder.agregarSnipers(Math.min(1 + (numero - 11) / 4, 3));
        }
        builder.definirVelocidad(velocidadDe(numero));
        builder.definirProbPowerUp(0.12f);

        // Las oleadas donde aparece algo nuevo avisan qué es
        if (numero == 1)
        {
            builder.definirAviso("BOT: lento y fácil. ¡Calienta!");
        }
        else if (numero == 2)
        {
            builder.definirAviso("NUEVO: LAG se teletransporta. Los bots ya disparan");
        }
        else if (numero == 3)
        {
            builder.definirAviso("NUEVO: CHEATER aguanta 3 balas y te apunta con 3 disparos");
        }
        else if (numero == 6)
        {
            builder.definirAviso("NUEVO: SPAMMER dispara ráfagas de 3 balas");
        }
        else if (numero == 11)
        {
            builder.definirAviso("NUEVO: SNIPER apunta con una línea roja. ¡Esquiva!");
        }
        return builder.getResult();
    }

    // Receta 2: oleada hardcore. Más enemigos y más rápidos, pero con más power-ups
    public Nivel construirOleadaHardcore(int numero)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre(nombreDe(numero));
        builder.agregarBots(Math.min(4 + numero / 3, 8));
        builder.agregarLags(Math.min(2 + numero / 5, 5));
        builder.agregarCheaters(Math.min(1 + numero / 6, 3));
        if (numero >= 6)
        {
            builder.agregarSpammers(Math.min(1 + (numero - 6) / 5, 3));
        }
        if (numero >= 11)
        {
            builder.agregarSnipers(Math.min(1 + (numero - 11) / 5, 2));
        }
        builder.definirVelocidad(velocidadDe(numero) + 0.1f);
        builder.definirProbPowerUp(0.18f);
        builder.definirAviso("HARDCORE: más enemigos y más rápidos, pero caen más power-ups");
        return builder.getResult();
    }

    // Receta 3: oleada de jefe. Un jefe con pocos enemigos de apoyo
    public Nivel construirOleadaJefe(int numero, TipoJefe jefe)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre(nombreDe(numero));
        builder.agregarJefe(jefe);
        if (numero == 5)
        {
            builder.agregarBots(2);
        }
        else if (numero == 10)
        {
            builder.agregarSpammers(2);
        }
        else if (numero == 15)
        {
            builder.agregarCheaters(2);
        }
        else
        {
            builder.agregarCheaters(2);
            builder.agregarLags(2);
        }
        builder.definirVelocidad(1.2f);
        builder.definirProbPowerUp(0.1f);
        if (numero == TOTAL_OLEADAS)
        {
            builder.definirAviso("¡ÚLTIMO JEFE! Si lo derrotas, ganas el juego");
        }
        else
        {
            builder.definirAviso("¡JEFE! Barra de vida arriba. Cuidado con sus ataques");
        }
        return builder.getResult();
    }

    // ¿Esta oleada es hardcore?
    private boolean esHardcore(int numero)
    {
        return numero == 4 || numero == 8 || numero == 12 || numero == 17 || numero == 19;
    }

    // Qué jefe toca en cada oleada de jefe (null si la oleada no tiene jefe)
    // Provisorio: el commit 32 agrega los jefes nuevos del 10 y del 15
    private TipoJefe jefeDe(int numero)
    {
        if (numero == 5 || numero == 10)
        {
            return TipoJefe.TOXIC;
        }
        if (numero == 15 || numero == 20)
        {
            return TipoJefe.ADMIN;
        }
        return null;
    }

    // Elige la receta según el número de oleada
    public Nivel construirOleada(int numero)
    {
        TipoJefe jefe = jefeDe(numero);
        if (jefe != null)
        {
            return construirOleadaJefe(numero, jefe);
        }
        if (esHardcore(numero))
        {
            return construirOleadaHardcore(numero);
        }
        return construirOleadaNormal(numero);
    }
}
