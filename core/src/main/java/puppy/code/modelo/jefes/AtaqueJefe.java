package puppy.code.modelo.jefes;

import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.enemigos.EnemigoJefe;

/*
    PATRÓN STRATEGY (aplicado a los jefes): un ataque del jefe.

    Cada jefe tiene una lista de ataques por fase y simplemente llama a actualizar() de cada
    uno en cada fotograma; cada ataque lleva su propia cuenta de tiempo y decide cuándo
    disparar. Así los jefes se arman combinando ataques (disparo apuntado, anillo, lluvia,
    rayos...) en vez de tener un código distinto y enorme para cada uno.

    Para crear un ataque nuevo basta una clase que implemente esta interfaz.
*/
public interface AtaqueJefe
{
    void actualizar(EnemigoJefe jefe, ContextoCombate contexto);
}
