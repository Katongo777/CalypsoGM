package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Ball2 extends Entidad
{

    public Ball2(int x, int y, int size, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx);

        // Validar que el borde de la esfera no quede fuera al inicio
        float nuevoX = x;
        float nuevoY = y;
        if (x - size < 0) nuevoX = x + size;
        if (x + size > Gdx.graphics.getWidth()) nuevoX = x - size;
        if (y - size < 0) nuevoY = y + size;
        if (y + size > Gdx.graphics.getHeight()) nuevoY = y - size;

        setPosicion(nuevoX, nuevoY);
    }

    // El método solo se encarga de procesar la lógica de movimiento
    @Override
    public void actualizar()
    {
        setPosicion(getX() + getXVel(), getY() + getYVel());
        comprobarLimitesPantalla();
    }

    // Método privado para ocultar la lógica de rebote contra los bordes
    private void comprobarLimitesPantalla()
    {
        if (getX() + getXVel() < 0 || getX() + getXVel() + getAncho() > Gdx.graphics.getWidth())
        {
            setXVel(getXVel() * -1);
        }
        if (getY() + getYVel() < 0 || getY() + getYVel() + getAlto() > Gdx.graphics.getHeight())
        {
            setYVel(getYVel() * -1);
        }
    }

    // Cuando choca con otro asteroide no manipula sus variables, solo avisa del choque
    public void revisarColisionAsteroide(Ball2 otroAsteroide)
    {
        if (this.chocaCon(otroAsteroide))
        {
            float miXVel = getXVel();
            float miYVel = getYVel();

            this.reaccionarChoqueAsteroide(otroAsteroide.getXVel(), otroAsteroide.getYVel());
            otroAsteroide.reaccionarChoqueAsteroide(miXVel, miYVel);
        }
    }

    // El asteroide es responsable de cambiar su propia velocidad
    public void reaccionarChoqueAsteroide(float xVelOtro, float yVelOtro)
    {
        if (getXVel() == 0) setXVel(getXVel() + xVelOtro / 2);
        setXVel(-getXVel());

        if (getYVel() == 0) setYVel(getYVel() + yVelOtro / 2);
        setYVel(-getYVel());
    }

    // El asteroide decide como rebotar al recibir daño.
    public void reaccionarChoqueNave(float xVelNave, float yVelNave)
    {
        if (getXVel() == 0) setXVel(getXVel() + xVelNave / 2);
        setXVel(-getXVel());

        if (getYVel() == 0) setYVel(getYVel() + yVelNave / 2);
        setYVel(-getYVel());
    }
}
