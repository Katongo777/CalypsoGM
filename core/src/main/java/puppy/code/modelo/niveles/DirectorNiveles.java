package puppy.code.modelo.niveles;

import puppy.code.modelo.enemigos.TipoJefe;

/*
    PATRÓN BUILDER (GM-9): este es el DIRECTOR.

    Conoce las "recetas": en qué orden y con qué valores hay que llamar al Builder
    para obtener cada tipo de oleada. El mismo proceso de construcción (el Builder)
    sirve para armar oleadas muy distintas.

    La campaña tiene 10 oleadas:
        1 a 3   normales (cada una presenta un enemigo nuevo)
        4       hardcore
        5       JEFE: El Toxic
        6 y 7   normales
        8       hardcore
        9       normal (la más difícil)
        10      JEFE FINAL: El Admin
*/
public class DirectorNiveles
{
    public static final int TOTAL_OLEADAS = 10;

    // Nombre de cada oleada (el índice 0 es la oleada 1)
    private static final String[] NOMBRES = {
        "Bots en el lobby",
        "Se cae el wifi",
        "Hay cheaters en la partida",
        "¡MODO HARDCORE!",
        "JEFE: EL TOXIC",
        "Partida clasificatoria",
        "Todos son tryhards",
        "¡HARDCORE: ELO HELL!",
        "La calma antes de la tormenta",
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

    // Receta 1: oleada normal. Va creciendo de a poco con el número de oleada
    public Nivel construirOleadaNormal(int numero)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre(nombreDe(numero));
        builder.agregarBots(Math.min(3 + numero, 10));
        if (numero >= 2)
        {
            builder.agregarLags(Math.min(1 + numero / 2, 5));
        }
        if (numero >= 3)
        {
            builder.agregarCheaters(Math.min(numero / 3, 3));
        }
        builder.definirVelocidad(Math.min(1f + 0.06f * numero, 1.6f));
        builder.definirProbPowerUp(0.12f);

        // Las primeras oleadas avisan qué enemigo es nuevo
        if (numero == 1)
        {
            builder.definirAviso("BOT: lento y fácil. ¡Calienta!");
        }
        else if (numero == 2)
        {
            builder.definirAviso("NUEVO ENEMIGO: LAG, se teletransporta");
        }
        else if (numero == 3)
        {
            builder.definirAviso("NUEVO ENEMIGO: CHEATER, aguanta 3 balas y suelta 2 Lags");
        }
        return builder.getResult();
    }

    // Receta 2: oleada hardcore. Más enemigos, más rápidos, pero más power-ups
    public Nivel construirOleadaHardcore(int numero)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre(nombreDe(numero));
        builder.agregarBots(Math.min(2 + numero, 8));
        builder.agregarLags(Math.min(2 + numero / 2, 6));
        builder.agregarCheaters(Math.min(1 + numero / 3, 3));
        builder.definirVelocidad(Math.min(1.15f + 0.06f * numero, 1.8f));
        builder.definirProbPowerUp(0.18f);
        builder.definirAviso("Más enemigos y más rápidos, pero caen más power-ups");
        return builder.getResult();
    }

    // Receta 3: oleada de jefe. Un jefe con pocos enemigos de apoyo
    public Nivel construirOleadaJefe(int numero, TipoJefe jefe)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre(nombreDe(numero));
        builder.agregarJefe(jefe);
        if (jefe == TipoJefe.TOXIC)
        {
            builder.agregarBots(2);
            builder.definirAviso("¡JEFE! Invoca Lags. Dispárale hasta vaciar su barra");
        }
        else
        {
            builder.agregarCheaters(2);
            builder.definirAviso("¡ÚLTIMO JEFE! Si lo derrotas, ganas el juego");
        }
        builder.definirVelocidad(1.3f);
        builder.definirProbPowerUp(0.1f);
        return builder.getResult();
    }

    // Elige la receta según el número de oleada
    public Nivel construirOleada(int numero)
    {
        if (numero == 5)
        {
            return construirOleadaJefe(numero, TipoJefe.TOXIC);
        }
        if (numero == TOTAL_OLEADAS)
        {
            return construirOleadaJefe(numero, TipoJefe.ADMIN);
        }
        if (numero == 4 || numero == 8)
        {
            return construirOleadaHardcore(numero);
        }
        return construirOleadaNormal(numero);
    }
}
