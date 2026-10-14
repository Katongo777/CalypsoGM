package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Ball2 extends Entidad
{
	private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;
    private Sprite spr;

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
        
        this.spr.setPosition(nuevoX, nuevoY);
    }
    
    // El método solo se encarga de procesar la lógica de movimiento
    @Override
    public void actualizar()
    {
        spr.setX(spr.getX() + xVel);
        spr.setY(spr.getY() + yVel);
        comprobarLimitesPantalla();
    }
    
    // Método privado para ocultar la lógica de rebote contra los bordes
    private void comprobarLimitesPantalla()
    {
        if (x + xSpeed < 0 || x + xSpeed + spr.getWidth() > Gdx.graphics.getWidth())
        {
            xSpeed = xSpeed * -1;
        }
        if (y + ySpeed < 0 || y + ySpeed + spr.getHeight() > Gdx.graphics.getHeight())
        {
            ySpeed = ySpeed * -1;
        }
    }
     
    // Cuando choca con otro asteroide no manipula sus variables, solo avisa del choque
    public void revisarColisionAsteroide(Ball2 otroAsteroide) {
        if(spr.getBoundingRectangle().overlaps(otroAsteroide.getArea()))
        {
            float miXVel = this.xVel;
            float miYVel = this.yVel;
            
            this.reaccionarChoqueAsteroide(otroAsteroide.getXVel(), otroAsteroide.getYVel());
            otroAsteroide.reaccionarChoqueAsteroide(miXVel, miYVel);
        }
    }
    
    // El asteroide es responsable de cambiar su propia velocidad
    public void reaccionarChoqueAsteroide(float xVelOtro, float yVelOtro)
    {
        if (this.xVel == 0) this.xVel += xVelOtro / 2;
        this.xVel = -this.xVel;
        
        if (this.yVel == 0) this.yVel += yVelOtro / 2;
        this.yVel = -this.yVel;
    }
    
    // El asteroide decide cómo rebotar al recibir daño.
    public void reaccionarChoqueNave(float xVelNave, float yVelNave)
    {
        if (this.xVel == 0) this.xVel += xVelNave / 2;
        this.xVel = -this.xVel;
        
        if (this.yVel == 0) this.yVel += yVelNave / 2;
        this.yVel = -this.yVel;
    }
}
