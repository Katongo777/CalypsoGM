package puppy.code.modelo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.Constantes;
import puppy.code.modelo.armas.EstrategiaDisparo;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.util.GestorRecursos;

/*
    La nave del jugador.

    Es el CONTEXTO del patrón Strategy: guarda una EstrategiaDisparo y la usa al
    disparar, sin saber qué arma es. Se puede cambiar con setEstrategia().

    ENCAPSULAMIENTO (GM-5): disparar() DEVUELVE las balas y la pantalla decide qué
    hacer con ellas; la nave no conoce a la vista.
*/
public class Nave4 extends Entidad
{
    private boolean destruida = false;
    private int vidas;
    private float velocidad;
    private Personaje personaje;
    private Sound sonidoHerido;
    private boolean herido = false;
    private int tiempoHeridoMax = 90;
    private int tiempoHerido;

    // Strategy: el arma actual y el tiempo de espera entre disparos
    private EstrategiaDisparo estrategia;
    private int cooldownRestante = 0;

    // Efectos de power-ups: cuántos fotogramas les quedan (0 = inactivo)
    private int escudoRestante = 0;
    private int turboRestante = 0;

    /*
        La nave se crea a partir del personaje elegido: de él salen la imagen,
        las vidas, la velocidad y el arma inicial.
    */
    public Nave4(Personaje personaje)
    {
        super(Constantes.ANCHO / 2 - 32, 30, 0, 0,
                GestorRecursos.getInstance().getTextura(personaje.getArchivoNave()));
        this.personaje = personaje;
        this.sonidoHerido = GestorRecursos.getInstance().getSonido("hurt.ogg");
        this.vidas = personaje.getVidas();
        this.velocidad = personaje.getVelocidad();
        this.estrategia = personaje.crearArma();
        setTamano(64, 64);
    }

    @Override
    public void actualizar()
    {
        manejarInputTeclado();
        actualizarPosicion();

        if (cooldownRestante > 0)
        {
            cooldownRestante--;
        }
        if (escudoRestante > 0)
        {
            escudoRestante--;
        }
        if (turboRestante > 0)
        {
            turboRestante--;
        }
        if (herido)
        {
            actualizarEstadoHerido();
        }
    }

    // Método privado para ocultar el funcionamiento de la nave
    private void manejarInputTeclado()
    {
        setXVel(0);
        setYVel(0);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) setXVel(-velocidad);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) setXVel(velocidad);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) setYVel(-velocidad);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) setYVel(velocidad);
    }

    /*
        Dispara con el arma actual. Si todavía está "recargando" (cooldown) devuelve
        una lista vacía. Quien reciba las balas decide dónde guardarlas.
    */
    public List<Bullet> disparar()
    {
        if (cooldownRestante > 0)
        {
            return new ArrayList<>();
        }
        // Con el turbo (RGB) la nave dispara al doble de velocidad
        cooldownRestante = turboRestante > 0 ? estrategia.getCooldown() / 2 : estrategia.getCooldown();
        return estrategia.disparar(getX() + getAncho() / 2, getY() + getAlto() - 8);
    }

    private void actualizarPosicion()
    {
        float nuevaX = getX() + getXVel();
        float nuevaY = getY() + getYVel();

        // Limita la posición para que no salga de la ventana
        if (nuevaX < 0) nuevaX = 0;
        if (nuevaX + getAncho() > Constantes.ANCHO) nuevaX = Constantes.ANCHO - getAncho();

        if (nuevaY < 0) nuevaY = 0;
        if (nuevaY + getAlto() > Constantes.ALTO) nuevaY = Constantes.ALTO - getAlto();

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

    // Funciona con cualquier tipo de Enemigo. Mientras está herido es invulnerable.
    public boolean revisarColision(Enemigo enemigo)
    {
        if (!herido && this.chocaCon(enemigo))
        {
            // Con el escudo activo el enemigo se destruye y la nave no recibe daño
            if (escudoRestante > 0)
            {
                return true;
            }
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

    // Mientras es invulnerable la nave parpadea (se salta el dibujo cada cierto tiempo)
    @Override
    public void dibujar(SpriteBatch batch)
    {
        if (herido && (tiempoHerido / 6) % 2 == 0)
        {
            return;
        }
        super.dibujar(batch);

        // Halo del escudo: parpadea en el último segundo para avisar que se acaba
        if (escudoRestante > 0 && (escudoRestante > 60 || (escudoRestante / 6) % 2 == 0))
        {
            Texture halo = GestorRecursos.getInstance().getTextura("escudo.png");
            batch.draw(halo, getX() - 16, getY() - 16, getAncho() + 32, getAlto() + 32);
        }
    }

    public boolean estaDestruido() { return destruida; }
    public boolean estaHerido() { return herido; }
    public Personaje getPersonaje() { return personaje; }
    public int getVidas() { return vidas; }
    public void setVidas(int vidas) { this.vidas = vidas; }

    // ---- Efectos que aplican los power-ups ----
    public void curar(int cantidad)
    {
        vidas = Math.min(vidas + cantidad, 8);
    }

    public void activarEscudo(int fotogramas)
    {
        escudoRestante = fotogramas;
    }

    public void activarTurbo(int fotogramas)
    {
        turboRestante = fotogramas;
    }

    public boolean tieneEscudo() { return escudoRestante > 0; }
    public boolean tieneTurbo() { return turboRestante > 0; }

    // Vuelve a poner la nave abajo y al centro (se usa al empezar cada oleada)
    public void reubicar()
    {
        setPosicion(Constantes.ANCHO / 2f - getAncho() / 2, 30);
        setXVel(0);
        setYVel(0);
    }

    // Strategy: se puede cambiar de arma en cualquier momento
    public EstrategiaDisparo getEstrategia() { return estrategia; }

    // Ley de Demeter: quien necesita el nombre del arma se lo pide a la nave, no a su estrategia
    public String getNombreArma() { return estrategia.getNombre(); }
    public void setEstrategia(EstrategiaDisparo estrategia) { this.estrategia = estrategia; }
}
