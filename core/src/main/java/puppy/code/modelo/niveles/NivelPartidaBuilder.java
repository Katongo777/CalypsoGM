package puppy.code.modelo.niveles;

/*
    PATRÓN BUILDER (GM-9): este es el CONSTRUCTOR CONCRETO.

    Implementa los pasos de NivelBuilder y guarda internamente el Nivel que va
    armando. Al llamar a reset() se empieza un nivel nuevo con valores por defecto.
*/
public class NivelPartidaBuilder implements NivelBuilder
{
    private Nivel nivel;

    public NivelPartidaBuilder()
    {
        reset();
    }

    @Override
    public void reset()
    {
        nivel = new Nivel();
        // Valores por defecto: sin enemigos, velocidad normal y sin power-ups
        nivel.setNumero(1);
        nivel.setNombre("Oleada");
        nivel.setVelocidad(1f);
        nivel.setProbPowerUp(0f);
    }

    @Override
    public void definirNumero(int numero)
    {
        nivel.setNumero(numero);
    }

    @Override
    public void definirNombre(String nombre)
    {
        nivel.setNombre(nombre);
    }

    @Override
    public void agregarBots(int cantidad)
    {
        nivel.setCantBots(nivel.getCantBots() + cantidad);
    }

    @Override
    public void agregarLags(int cantidad)
    {
        nivel.setCantLags(nivel.getCantLags() + cantidad);
    }

    @Override
    public void agregarCheaters(int cantidad)
    {
        nivel.setCantCheaters(nivel.getCantCheaters() + cantidad);
    }

    @Override
    public void definirVelocidad(float velocidad)
    {
        nivel.setVelocidad(velocidad);
    }

    @Override
    public void definirProbPowerUp(float probabilidad)
    {
        nivel.setProbPowerUp(probabilidad);
    }

    @Override
    public Nivel getResult()
    {
        return nivel;
    }
}
