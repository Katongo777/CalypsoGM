package puppy.code.modelo.niveles;

/*
    PATRÓN BUILDER (GM-9): esta es la INTERFAZ BUILDER.

    Declara los pasos para construir un Nivel. Quien la use no necesita saber
    cómo se guarda cada dato por dentro, solo llama a los pasos que le interesan
    (los pasos que no llame quedan en su valor por defecto).

    Participantes del patrón en este proyecto:
        Builder:          NivelBuilder (esta interfaz)
        ConcreteBuilder:  NivelPartidaBuilder
        Director:         DirectorNiveles (define las "recetas" de cada oleada)
        Product:          Nivel
*/
public interface NivelBuilder
{
    // Empieza un nivel nuevo, vacío
    void reset();

    void definirNumero(int numero);

    void definirNombre(String nombre);

    void agregarBots(int cantidad);

    void agregarLags(int cantidad);

    void agregarCheaters(int cantidad);

    void definirVelocidad(float velocidad);

    void definirProbPowerUp(float probabilidad);

    // Entrega el nivel terminado
    Nivel getResult();
}
