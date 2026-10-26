package puppy.code.modelo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.Constantes;
import puppy.code.modelo.armas.EstrategiaDisparo;
import puppy.code.modelo.enemigos.Enemigo;
import puppy.code.modelo.habilidades.Habilidad;
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
    private boolean herido = false;
    private boolean apareciendo = false; // animación de entrada al empezar cada oleada
    private int tiempoHeridoMax = 180; // 3 segundos de invulnerabilidad después de un golpe
    private int tiempoHerido;

    // Strategy: el arma actual y el tiempo de espera entre disparos
    private EstrategiaDisparo estrategia;
    private EstrategiaDisparo armaOriginal;   // el arma del personaje, a la que se vuelve
    private int armaTemporalRestante = 0;      // fotogramas que quedan de un arma temporal
    private int cooldownRestante = 0;

    // Habilidad especial: una barra de energía (0 a 100) que se llena eliminando enemigos
    public static final float ENERGIA_MAXIMA = 100f;
    private Habilidad habilidad;
    private float energia = 0;

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
        this.vidas = personaje.getVidas();
        this.velocidad = personaje.getVelocidad();
        this.estrategia = personaje.crearArma();
        this.armaOriginal = estrategia;
        this.habilidad = personaje.crearHabilidad();
        setTamano(64, 64);
        setFactorHitbox(0.55f); // solo el cuerpo de la nave cuenta para los choques
    }

    @Override
    public void actualizar()
    {
        if (apareciendo)
        {
            // Animación de entrada: la nave sube desde abajo y todavía no se puede controlar
            setPosicion(getX(), getY() + 4);
            if (getY() >= 30)
            {
                setPosicion(getX(), 30);
                apareciendo = false;
            }
        }
        else
        {
            manejarInputTeclado();
            actualizarPosicion();
        }

        if (cooldownRestante > 0)
        {
            cooldownRestante--;
        }
        if (armaTemporalRestante > 0)
        {
            armaTemporalRestante--;
            if (armaTemporalRestante == 0)
            {
                estrategia = armaOriginal; // se acabó el arma temporal: vuelve la del personaje
            }
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
        if (cooldownRestante > 0 || apareciendo)
        {
            return new ArrayList<>();
        }
        // Con el turbo (RGB) la nave dispara al doble de velocidad
        cooldownRestante = turboRestante > 0 ? estrategia.getCooldown() / 2 : estrategia.getCooldown();
        List<Bullet> balas = estrategia.disparar(getX() + getAncho() / 2, getY() + getAlto() - 8);

        // Para que la invulnerabilidad no sea una herramienta "chetada": mientras dura, las balas
        // hacen la mitad de daño
        if (herido)
        {
            for (Bullet b : balas)
            {
                b.escalarDanio(0.5f);
            }
        }
        return balas;
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

    // Choque con un enemigo (el parámetro es un Enemigo para que quede claro el uso)
    public boolean revisarColision(Enemigo enemigo)
    {
        return revisarGolpe(enemigo);
    }

    /*
        Sirve para cualquier cosa que le pueda hacer daño a la nave (enemigos, balas enemigas...):
        basta que sea Colisionable. Mientras está herido es invulnerable. Devuelve true si la
        nave fue tocada (aunque el escudo haya evitado el daño).
    */
    public boolean revisarGolpe(Colisionable fuente)
    {
        if (!herido && this.chocaCon(fuente))
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
        perderEnergia(25); // cada vez que te golpean, la barra de la habilidad baja un poco
        herido = true;
        tiempoHerido = tiempoHeridoMax;
        GestorRecursos.getInstance().reproducirSonido("hurt.ogg");
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

    // ---- Habilidad especial ----
    public void sumarEnergia(float cantidad)
    {
        energia = Math.min(ENERGIA_MAXIMA, energia + cantidad);
    }

    public void perderEnergia(float cantidad)
    {
        energia = Math.max(0, energia - cantidad);
    }

    public boolean habilidadLista()
    {
        return energia >= ENERGIA_MAXIMA;
    }

    /*
        Usa la habilidad si la barra está llena (y la nave no está apareciendo). Después la
        barra queda en cero. Devuelve true si se activó.
    */
    public boolean usarHabilidad(ContextoCombate contexto)
    {
        if (!habilidadLista() || apareciendo)
        {
            return false;
        }
        habilidad.activar(this, contexto);
        energia = 0;
        return true;
    }

    public float getEnergia() { return energia; }
    public Habilidad getHabilidad() { return habilidad; }

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

    /*
        Animación de entrada al empezar cada oleada: la nave parte debajo de la pantalla,
        sube hasta su lugar y mientras tanto parpadea y es invulnerable.
    */
    public void aparecer()
    {
        setPosicion(Constantes.ANCHO / 2f - getAncho() / 2, -getAlto() - 20);
        setXVel(0);
        setYVel(0);
        apareciendo = true;
        herido = true;       // reutiliza el parpadeo y la invulnerabilidad del estado "herido"
        tiempoHerido = 200;
    }

    public boolean estaApareciendo() { return apareciendo; }

    // Strategy: se puede cambiar de arma en cualquier momento
    public EstrategiaDisparo getEstrategia() { return estrategia; }

    // Ley de Demeter: quien necesita el nombre del arma se lo pide a la nave, no a su estrategia
    public String getNombreArma() { return estrategia.getNombre(); }
    // Cambio permanente de arma
    public void setEstrategia(EstrategiaDisparo estrategia)
    {
        this.estrategia = estrategia;
        this.armaOriginal = estrategia;
        this.armaTemporalRestante = 0;
    }

    // Cambio temporal: usa la nueva arma unos segundos y después vuelve a la original
    public void equiparArmaTemporal(EstrategiaDisparo arma, int fotogramas)
    {
        this.estrategia = arma;
        this.armaTemporalRestante = fotogramas;
    }
}
