package puppy.code.modelo.habilidades;

import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.Nave4;

/*
    PATRÓN STRATEGY (otra vez): la habilidad especial de cada personaje.

    Es una interfaz con varias implementaciones (Overdrive, TiroCertero y Burbuja).
    La nave guarda una Habilidad y la usa con activar() sin saber cuál es: el personaje
    elegido decide qué clase le toca (ver Personaje.crearHabilidad()).

    Las armas son estrategias para "cómo disparo" y las habilidades son estrategias
    para "qué hago cuando se llena la barra". Cada una sigue el principio Abierto/Cerrado:
    para agregar una habilidad nueva se crea una clase y no se toca la nave.
*/
public interface Habilidad
{
    // Hace el efecto de la habilidad usando la nave y el contexto del combate
    void activar(Nave4 nave, ContextoCombate contexto);

    String getNombre();

    String getDescripcion();
}
