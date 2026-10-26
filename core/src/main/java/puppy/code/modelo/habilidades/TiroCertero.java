package puppy.code.modelo.habilidades;

import puppy.code.modelo.Bullet;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.Nave4;
import puppy.code.util.GestorRecursos;

/*
    Habilidad del Camper: un rayo gigante hacia arriba que atraviesa todo y hace mucho daño
    ("una bala, una baja"). Deja la bala en la lista de balas del jugador del contexto.
*/
public class TiroCertero implements Habilidad
{
    @Override
    public void activar(Nave4 nave, ContextoCombate contexto)
    {
        float x = nave.getX() + nave.getAncho() / 2;
        float y = nave.getY() + nave.getAlto() - 8;
        // 100 de ancho, 320 de alto, 12 de daño y atraviesa: golpea a todos los enemigos de la columna
        contexto.getBalasJugador().add(new Bullet(x - 50, y, 0, 26,
                GestorRecursos.getInstance().getTextura("bala_railgun.png"), 100, 320, 12, true));
    }

    @Override
    public String getNombre()
    {
        return "Tiro certero";
    }

    @Override
    public String getDescripcion()
    {
        return "un rayo gigante que atraviesa todo";
    }
}
