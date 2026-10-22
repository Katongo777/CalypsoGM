package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/*
    La nave del jugador.

    ENCAPSULAMIENTO (GM-5): antes esta clase conocia a PantallaJuego (una clase de
    la vista) y le entregaba las balas con juego.agregarBala(...). Eso es una mala
    dependencia: el modelo no debe saber que existe la vista. Ahora disparar()
    simplemente DEVUELVE la bala y la pantalla decide que hacer con ella.
*/
public class Nave4 extends Entidad
{
    private boolean destruida = false;
    private int vidas = 3;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;

    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala)
    {
        super(x, y, 0, 0, tx); // La nave inicia con velocidad 0
        this.sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        this.txBala = txBala;
        setTamano(45, 45);
    }

    // Este método solo procesa datos y estado (ya no recibe la pantalla)
    @Override
    public void actualizar()
    {
        if (!herido)
        {
            manejarInputTeclado();
            actualizarPosicion();
        }
        else
        {
            actualizarEstadoHerido();
        }
    }

    // Método privado para ocultar el funcionamiento de la nave
    private void manejarInputTeclado()
    {
        setXVel(0);
        setYVel(0);

        float velFija = 5f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) setXVel(-velFija);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) setXVel(velFija);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) setYVel(-velFija);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) setYVel(velFija);
    }

    // Crea la bala y la devuelve; quien la reciba decide donde guardarla
    public Bullet disparar()
    {
        Bullet bala = new Bullet(getX() + getAncho() / 2 - 5, getY() + getAlto() - 5, 0, 3, txBala);
        soundBala.play();
        return bala;
    }

    private void actualizarPosicion()
    {
        float nuevaX = getX() + getXVel();
        float nuevaY = getY() + getYVel();

        // Limita la posición para que no salga de la ventana
        if (nuevaX < 0) nuevaX = 0;
        if (nuevaX + getAncho() > Gdx.graphics.getWidth()) nuevaX = Gdx.graphics.getWidth() - getAncho();

        if (nuevaY < 0) nuevaY = 0;
        if (nuevaY + getAlto() > Gdx.graphics.getHeight()) nuevaY = Gdx.graphics.getHeight() - getAlto();

        setPosicion(nuevaX, nuevaY);
    }

    private void actualizarEstadoHerido()
    {
        tiempoHerido--;
        if (tiempoHerido <= 0)
        {
            herido = false;
        }
    }

    // Delegamos la responsabilidad a Ball
    public boolean revisarColision(Ball2 b)
    {
        if (!herido && this.chocaCon(b))
        {
            setXVel(-getXVel());
            setYVel(-getYVel());

            b.reaccionarChoqueNave(getXVel(), getYVel());

            recibirDano();
            return true;
        }
        return false;
    }

    // Agrupa toda la lógica de recibir daño
    private void recibirDano()
    {
        vidas--;
        herido = true;
        tiempoHerido = tiempoHeridoMax;
        sonidoHerido.play();
        if (vidas <= 0)
        {
            destruida = true;
        }
    }

    // Método para renderizar la nave. El Override es para el temblor al recibir daño
    @Override
    public void dibujar(SpriteBatch batch)
    {
        if (herido)
        {
            float xOriginal = getX();
            setPosicion(xOriginal + MathUtils.random(-2, 2), getY());
            super.dibujar(batch);
            setPosicion(xOriginal, getY());
        }
        else
        {
            super.dibujar(batch);
        }
    }

    public boolean estaDestruido() { return !herido && destruida; }
    public boolean estaHerido() { return herido; }
    public int getVidas() { return vidas; }
    public void setVidas(int vidas) { this.vidas = vidas; }
}
