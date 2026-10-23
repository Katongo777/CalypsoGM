package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Texture;

/*
    El enemigo básico: un "bot" que rebota por la pantalla en línea recta.
    Antes esta clase se llamaba Ball2 y era la única; ahora es una más de la
    jerarquía de enemigos (hereda de Enemigo).
*/
public class EnemigoBot extends Enemigo
{
    public EnemigoBot(int x, int y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 1, 10); // 1 de vida, da 10 puntos
        setTamano(64, 64);
        ubicarDentroDePantalla();
    }

    @Override
    public String getNombre()
    {
        return "Bot";
    }

    // Se mueve en línea recta y rebota contra los bordes
    @Override
    public void actualizar()
    {
        moverConRebote();
    }
}
