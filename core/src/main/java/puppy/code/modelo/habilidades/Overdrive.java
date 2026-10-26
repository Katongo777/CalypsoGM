package puppy.code.modelo.habilidades;

import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.Nave4;
import puppy.code.modelo.armas.Escopeta;

/*
    Habilidad del Tryhard: durante 6 segundos dispara con la escopeta al doble de velocidad
    ("modo tryhard"). Usa los efectos que ya tenía la nave: arma temporal y turbo.
*/
public class Overdrive implements Habilidad
{
    private static final int DURACION = 360; // 6 segundos (60 fotogramas por segundo)

    @Override
    public void activar(Nave4 nave, ContextoCombate contexto)
    {
        nave.equiparArmaTemporal(new Escopeta(), DURACION);
        nave.activarTurbo(DURACION);
    }

    @Override
    public String getNombre()
    {
        return "Overdrive";
    }

    @Override
    public String getDescripcion()
    {
        return "6 segundos de escopeta al doble de velocidad";
    }
}
