package puppy.code.modelo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entidad
{    
    protected Sprite spr;
    protected float xVel;
    protected float yVel;

    public Entidad(float x, float y, float xVel, float yVel, Texture tx)
    {
        this.spr = new Sprite(tx);
        this.spr.setPosition(x, y);
        this.xVel = xVel;
        this.yVel = yVel;
    }

    // Cada hijo debe definir su propia forma de moverse
    public abstract void actualizar();

    public void dibujar(SpriteBatch batch)
    {
        spr.draw(batch);
    }

    public Rectangle getArea()
    {
        return spr.getBoundingRectangle();
    }

    // Getters y Setters
    public float getX() { return spr.getX(); }
    public float getY() { return spr.getY(); }
    public float getXVel() { return xVel; }
    public float getYVel() { return yVel; }
    public void setXVel(float xVel) { this.xVel = xVel; }
    public void setYVel(float yVel) { this.yVel = yVel; }
}