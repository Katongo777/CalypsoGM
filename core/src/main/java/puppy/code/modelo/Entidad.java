package puppy.code.modelo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/*
    CLASE ABSTRACTA (GM-4) y ENCAPSULAMIENTO (GM-5)

    Entidad es el "padre" de todo lo que se mueve y se dibuja en el juego
    (la nave, las balas, los enemigos...). Guarda lo que tienen en comun:
    una imagen (sprite), una posicion y una velocidad.

    Encapsulamiento: los atributos son PRIVADOS. Los hijos no los tocan
    directamente, usan los metodos de abajo. Asi, si algun dia cambiamos
    como se guarda la posicion, solo se modifica esta clase.
*/
public abstract class Entidad implements Colisionable
{
    private Sprite spr;
    private float xVel;
    private float yVel;
    private float factorHitbox = 1f; // qué parte del dibujo cuenta para los choques (1 = todo)

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

    // Método de la interfaz Colisionable
    /*
        La hitbox puede ser más chica que el dibujo: las imágenes tienen bordes transparentes y
        sin este ajuste los choques se sienten injustos ("me pegó sin tocarme").
    */
    @Override
    public Rectangle getArea()
    {
        Rectangle r = spr.getBoundingRectangle();
        if (factorHitbox == 1f)
        {
            return r;
        }
        float ancho = r.width * factorHitbox;
        float alto = r.height * factorHitbox;
        return new Rectangle(r.x + (r.width - ancho) / 2, r.y + (r.height - alto) / 2, ancho, alto);
    }

    // Método de la interfaz Colisionable: hay choque si las hitbox se superponen
    @Override
    public boolean chocaCon(Colisionable otro)
    {
        return this.getArea().overlaps(otro.getArea());
    }

    // ---- Métodos públicos de lectura y escritura (getters y setters) ----
    public float getX() { return spr.getX(); }
    public float getY() { return spr.getY(); }
    public float getAncho() { return spr.getWidth(); }
    public float getAlto() { return spr.getHeight(); }
    public float getXVel() { return xVel; }
    public float getYVel() { return yVel; }
    public void setXVel(float xVel) { this.xVel = xVel; }
    public void setYVel(float yVel) { this.yVel = yVel; }

    // ---- Métodos protegidos: solo los pueden usar las clases hijas ----
    protected void setPosicion(float x, float y)
    {
        spr.setPosition(x, y);
    }

    // Tiñe la imagen (el color se multiplica con los colores del dibujo; 1,1,1,1 = sin tinte)
    protected void setTinte(float r, float g, float b, float a)
    {
        spr.setColor(r, g, b, a);
    }

    protected void setFactorHitbox(float factor)
    {
        this.factorHitbox = factor;
    }

    protected void setTamano(float ancho, float alto)
    {
        spr.setSize(ancho, alto);
        spr.setOriginCenter(); // para que al rotar gire sobre su centro
    }

    // Gira la imagen (en grados) sobre su centro; solo cambia el dibujo, no la hitbox
    protected void rotar(float grados)
    {
        spr.rotate(grados);
    }
}
