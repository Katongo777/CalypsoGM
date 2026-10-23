package puppy.code.modelo.armas;

import java.util.List;

import puppy.code.modelo.Bullet;

/*
    PATRÓN STRATEGY (GM-7) e INTERFAZ (GM-4)

    Problema: la nave disparaba siempre de la misma forma, con el código de la bala
    metido dentro de Nave4. Si queríamos otra arma (escopeta, railgun...) había que
    llenar Nave4 de "if" para saber cuál estaba usando.

    Solución: encapsulamos cada forma de disparar en su propia clase. Todas cumplen
    este contrato (la interfaz), así que la nave puede cambiar de arma en pleno juego
    sin saber cuál es: solo llama a disparar().

        Contexto (Context):          Nave4
        Estrategia (Strategy):       EstrategiaDisparo   (esta interfaz)
        Estrategias concretas:       Pistola, Escopeta, Railgun, BFG...

    Para agregar un arma nueva basta crear una clase nueva que implemente esta
    interfaz; no hay que tocar Nave4 (principio Abierto/Cerrado de SOLID).
*/
public interface EstrategiaDisparo
{
    // Crea las balas del disparo. (x, y) es el punto de salida, en la punta de la nave
    List<Bullet> disparar(float x, float y);

    // Cuántos fotogramas hay que esperar entre un disparo y el siguiente
    int getCooldown();

    // Nombre del arma (para mostrarlo en pantalla)
    String getNombre();
}
