package puppy.code.modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.vista.PantallaJuego;



public class Nave4 extends Entidad
{
	private boolean destruida = false;
    private int vidas = 3;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax=50;
    private int tiempoHerido;
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala)
    {
        super(x, y, 0, 0, tx); // La nave inicia con velocidad 0
        this.sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        this.txBala = txBala;
        this.spr.setBounds(x, y, 45, 45);
    }
    
    // Este método solo procesa datos y estado
    @Override
    public void actualizar()
    {
        actualizarPosicion();
    }
    
    public void actualizar(PantallaJuego juego)
    {
        if (!herido)
        {
            manejarInputTeclado(juego);
            actualizar();
        }
        else
        {
            actualizarEstadoHerido();
        }
    }
    
    // Método privado para ocultar el funcionamiento de la nave
    private void manejarInputTeclado(PantallaJuego juego)
    {
    	xVel = 0;
    	yVel = 0;
    	
    	float velFija = 5f;
    	
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) xVel = -velFija;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) xVel = velFija;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) yVel = -velFija;     
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) yVel = velFija;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            disparar(juego);
        }
    }
    
    private void disparar(PantallaJuego juego)
    {
        Bullet bala = new Bullet(spr.getX() + spr.getWidth() / 2 - 5, spr.getY() + spr.getHeight() - 5, 0, 3, txBala);
        juego.agregarBala(bala);
        soundBala.play();
    }
    
    private void actualizarPosicion()
    {
        float x = spr.getX();
        float y = spr.getY();
        
        float nuevaX = x + xVel;
        float nuevaY = y + yVel;
        
        // Limita la posición para que no salga de la ventana
        if (nuevaX < 0) nuevaX = 0;
        if (nuevaX + spr.getWidth() > Gdx.graphics.getWidth()) nuevaX = Gdx.graphics.getWidth() - spr.getWidth();
        
        if (nuevaY < 0) nuevaY = 0;
        if (nuevaY + spr.getHeight() > Gdx.graphics.getHeight()) nuevaY = Gdx.graphics.getHeight() - spr.getHeight();
        
        spr.setPosition(nuevaX, nuevaY);  
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
            
            xVel = -xVel;
            yVel = -yVel;
            
            b.reaccionarChoqueNave(xVel, yVel); 
            
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
      
    // Método para renderizar Nave. El Override es para el temblor al recibir daño
    @Override
    public void dibujar(SpriteBatch batch)
    {
        if (herido)
        {
            float xOriginal = spr.getX();
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            super.dibujar(batch);
            spr.setX(xOriginal); 
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
