package puppy.code.modelo.powerups;

/*
    Sabe crear UN tipo de power-up en una posición. Es una interfaz de un solo método,
    así que se puede escribir como referencia a constructor (Pizza::new, RGB::new...).
*/
public interface FabricaPowerUp
{
    PowerUp crear(float x, float y);
}
