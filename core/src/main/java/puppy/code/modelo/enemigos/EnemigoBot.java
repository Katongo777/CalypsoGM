package puppy.code.modelo.enemigos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/*
    El enemigo básico: un "bot" que rebota por la pantalla.
    Antes esta clase se llamaba Ball2 y era la única; ahora es una más de la
    jerarquía de enemigos (hereda de Enemigo).
*/
public class EnemigoBot extends Enemigo
{
    public EnemigoBot(int x, int y, int size, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 1, 10); // 1 de vida, da 10 puntos

        // Validar que el borde no quede fuera de la ventana al inicio
        float nuevoX = x;
        float nuevoY = y;
        if (x - size < 0) nuevoX = x + size;
        if (x + size > Gdx.graphics.getWidth()) nuevoX = x - size;
        if (y - size < 0) nuevoY = y + size;
        if (y + size > Gdx.graphics.getHeight()) nuevoY = y - size;

        setPosicion(nuevoX, nuevoY);
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
