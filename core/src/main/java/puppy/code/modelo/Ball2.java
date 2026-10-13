package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Ball2
{
	private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;
    private Sprite spr;

    public Ball2(int x, int y, int size, int xSpeed, int ySpeed, Texture tx)
    {
    	spr = new Sprite(tx);
    	 
 	
        // Validar que borde de la esfera no quede fuera
    	this.x = x;
    	if (x - size < 0) this.x = x + size;
    	if (x + size > Gdx.graphics.getWidth())this.x = x - size;
        
    	// Validar que borde de la esfera no quede fuera
        this.y = y;
    	if (y-size < 0) this.y = y+size;
    	if (y+size > Gdx.graphics.getHeight())this.y = y-size;
    	
        spr.setPosition(x, y);
        this.setXSpeed(xSpeed);
        this.setySpeed(ySpeed);
    }
    
    // El método solo se encarga de procesar la lógica de movimiento
    public void actualizar()
    {
        x += getXSpeed();
        y += getySpeed();

        if (x+getXSpeed() < 0 || x+getXSpeed()+spr.getWidth() > Gdx.graphics.getWidth())
        	setXSpeed(getXSpeed() * -1);
        if (y+getySpeed() < 0 || y+getySpeed()+spr.getHeight() > Gdx.graphics.getHeight())
        	setySpeed(getySpeed() * -1);
        spr.setPosition(x, y);
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

    public void dibujar(SpriteBatch batch)
    {
        spr.draw(batch);
    }
     
    // Cuando choca con otro asteroide no manipula sus variables, solo avisa del choque
    public void revisarColisionAsteroide(Ball2 otroAsteroide)
    {
        if(spr.getBoundingRectangle().overlaps(otroAsteroide.getArea()))
        {
            
            // Guardamos nuestras velocidades para enviarselas al otro asteroide
            int miXVel = this.xSpeed;
            int miYVel = this.ySpeed;
            
            // Reaccionamos nosotros al choque con la velocidad del otro
            this.reaccionarChoqueAsteroide(otroAsteroide.getXSpeed(), otroAsteroide.getySpeed());
            
            // Le decimos al otro asteroide que reaccione pasándole nuestra velocidad original
            otroAsteroide.reaccionarChoqueAsteroide(miXVel, miYVel);
        }
    }
    
    // El asteroide es responsable de cambiar su propia velocidad
    public void reaccionarChoqueAsteroide(int xVelOtro, int yVelOtro)
    {
        if (this.xSpeed == 0)
        {
            this.xSpeed += xVelOtro / 2;
        }
        this.xSpeed = -this.xSpeed;
        
        if (this.ySpeed == 0)
        {
            this.ySpeed += yVelOtro / 2;
        }
        this.ySpeed = -this.ySpeed;
    }
    
    // El asteroide decide cómo rebotar al recibir daño.
    public void reaccionarChoqueNave(float xVelNave, float yVelNave)
    {
        if (this.xSpeed == 0)
        {
            this.xSpeed += (int) xVelNave / 2;
        }
        this.xSpeed = -this.xSpeed;
        
        if (this.ySpeed == 0)
        {
            this.ySpeed += (int) yVelNave / 2;
        }
        this.ySpeed = -this.ySpeed;
    }
    
    public Rectangle getArea()
    {
    	return spr.getBoundingRectangle();
    }
    
    // Getters y Setters
    public int getXSpeed() { return xSpeed; }
    public void setXSpeed(int xSpeed) { this.xSpeed = xSpeed; }
    public int getySpeed() { return ySpeed; }
    public void setySpeed(int ySpeed) { this.ySpeed = ySpeed; }
}
