package puppy.code.modelo.habilidades;

import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.Nave4;

/*
    Habilidad del Main Support: cura 1 vida, activa un escudo de 5 segundos y borra todas las
    balas enemigas de la pantalla ("cura con amor").
*/
public class Burbuja implements Habilidad
{
    @Override
    public void activar(Nave4 nave, ContextoCombate contexto)
    {
        nave.curar(1);
        nave.activarEscudo(300);
        contexto.getBalasEnemigas().clear();
    }

    @Override
    public String getNombre()
    {
        return "Burbuja";
    }

    @Override
    public String getDescripcion()
    {
        return "cura 1 vida, escudo y borra las balas enemigas";
    }
}
