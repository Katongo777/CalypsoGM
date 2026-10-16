package puppy.code.modelo;

import com.badlogic.gdx.math.Rectangle;

public interface Colisionable
{
    // Cualquier objeto colisionable debe tener una hitbox
    Rectangle getArea();
    
    // Cualquier objeto colisionable debe calcular si se cruza con otro
    boolean chocaCon(Colisionable otro);
}