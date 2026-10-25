package puppy.code.modelo.powerups;

import com.badlogic.gdx.math.MathUtils;

import puppy.code.modelo.Nave4;
import puppy.code.modelo.armas.BFG;
import puppy.code.modelo.armas.Escopeta;
import puppy.code.modelo.armas.EstrategiaDisparo;
import puppy.code.modelo.armas.Railgun;
import puppy.code.util.GestorRecursos;

/*
    La caja de botín (loot box): el chiste de la "gacha". Al abrirla te toca un
    arma al azar durante 15 segundos (900 fotogramas):
        - Escopeta (40%), Railgun (40%) o... el BFG 9000 (20%, la más rara).

    Usa lo que ya dejó listo la nave: equiparArmaTemporal(). Gracias al patrón
    Strategy, a la caja le da igual qué arma sea: todas son una EstrategiaDisparo.
*/
public class LootBox extends PowerUp
{
    private String nombreArma = "";

    public LootBox(float x, float y)
    {
        super(x, y, GestorRecursos.getInstance().getTextura("pw_lootbox.png"));
    }

    @Override
    public void aplicar(Nave4 nave)
    {
        float suerte = MathUtils.random();
        EstrategiaDisparo arma;
        if (suerte < 0.4f)
        {
            arma = new Escopeta();
        }
        else if (suerte < 0.8f)
        {
            arma = new Railgun();
        }
        else
        {
            arma = new BFG();
        }
        nombreArma = arma.getNombre();
        nave.equiparArmaTemporal(arma, 900);
    }

    @Override
    public String getMensaje()
    {
        return "¡LOOT BOX! Te tocó: " + nombreArma;
    }
}
