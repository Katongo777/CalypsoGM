package puppy.code.modelo.niveles;

/*
    PATRÓN BUILDER (GM-9): este es el DIRECTOR.

    Conoce las "recetas": en qué orden y con qué valores hay que llamar al Builder
    para obtener cada tipo de oleada. El mismo proceso de construcción (el Builder)
    sirve para armar oleadas muy distintas.

    Cada 3 oleadas viene una oleada HARDCORE; las demás son normales.
*/
public class DirectorNiveles
{
    private NivelBuilder builder;

    public DirectorNiveles(NivelBuilder builder)
    {
        this.builder = builder;
    }

    // Receta 1: oleada normal. Va creciendo de a poco con el número de oleada
    public Nivel construirOleadaNormal(int numero)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre("Oleada " + numero + ": bots en el lobby");
        builder.agregarBots(Math.min(3 + numero, 10));
        if (numero >= 2)
        {
            builder.agregarLags(Math.min(numero - 1, 8));
        }
        if (numero >= 3)
        {
            builder.agregarCheaters(Math.min(numero / 3, 4));
        }
        builder.definirVelocidad(Math.min(1f + 0.06f * numero, 1.6f));
        builder.definirProbPowerUp(0.25f);
        return builder.getResult();
    }

    // Receta 2: oleada hardcore. Más enemigos, más rápidos, pero más power-ups
    public Nivel construirOleadaHardcore(int numero)
    {
        builder.reset();
        builder.definirNumero(numero);
        builder.definirNombre("Oleada " + numero + ": ¡MODO HARDCORE!");
        builder.agregarBots(Math.min(2 + numero, 8));
        builder.agregarLags(Math.min(2 + numero / 2, 6));
        builder.agregarCheaters(Math.min(1 + numero / 3, 3));
        builder.definirVelocidad(Math.min(1.15f + 0.06f * numero, 1.8f));
        builder.definirProbPowerUp(0.4f);
        return builder.getResult();
    }

    // Elige la receta según el número de oleada
    public Nivel construirOleada(int numero)
    {
        if (numero % 3 == 0)
        {
            return construirOleadaHardcore(numero);
        }
        return construirOleadaNormal(numero);
    }
}
