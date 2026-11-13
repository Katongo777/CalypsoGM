package puppy.code.modelo.powerups;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;

/*
    Decide qué power-up cae cuando un enemigo suelta uno.

    Antes PantallaJuego tenía un switch que hacía "new Pizza", "new RGB"... y conocía todas
    las clases concretas. Ahora la pantalla solo conoce este generador y la clase abstracta
    PowerUp. Agregar un power-up nuevo es agregar UNA línea en la lista de abajo, sin tocar
    ninguna otra clase (principio Abierto/Cerrado).
*/
public class GeneradorPowerUps
{
    private final List<FabricaPowerUp> fabricas = new ArrayList<>();
    private final FabricaPowerUp fabricaCuracion;

    public GeneradorPowerUps()
    {
        fabricaCuracion = Pizza::new;
        fabricas.add(fabricaCuracion);
        fabricas.add(SillaGamer::new);
        fabricas.add(LootBox::new);
        fabricas.add(RGB::new);
    }

    // Un power-up cualquiera, todos con la misma probabilidad
    public PowerUp crearAlAzar(float x, float y)
    {
        return fabricas.get(MathUtils.random(fabricas.size() - 1)).crear(x, y);
    }

    // El que cura (una pizza): se usa cuando el jugador está en apuros
    public PowerUp crearCuracion(float x, float y)
    {
        return fabricaCuracion.crear(x, y);
    }
}
