package puppy.code.modelo.enemigos;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.Constantes;
import puppy.code.modelo.ContextoCombate;
import puppy.code.modelo.EfectosCombate;
import puppy.code.modelo.jefes.AtaqueAnillo;
import puppy.code.modelo.jefes.AtaqueApuntado;
import puppy.code.modelo.jefes.AtaqueEmbestida;
import puppy.code.modelo.jefes.AtaqueEspiral;
import puppy.code.modelo.jefes.AtaqueInvocar.Apoyo;
import puppy.code.modelo.jefes.AtaqueInvocar;
import puppy.code.modelo.jefes.AtaqueJefe;
import puppy.code.modelo.jefes.AtaqueLaser;
import puppy.code.modelo.jefes.AtaqueLluvia;
import puppy.code.modelo.jefes.AtaqueSalto;
import puppy.code.util.GestorRecursos;
import puppy.code.vista.BarraJefe;

/*
    El JEFE: un enemigo grande con mucha vida y varias FASES.

    Cada vez que su vida baja de ciertos umbrales (por ejemplo, la mitad) pasa de fase: se pone
    un ESCUDO (es invulnerable) y durante ese momento lanza un ataque especial e invoca enemigos
    de apoyo. El escudo se rompe cuando derrotas a todo el apoyo (o si pasa mucho tiempo), y
    el jefe sigue peleando con ataques más fuertes.

    Qué ataques usa cada jefe depende de su TipoJefe, y los ataques son objetos intercambiables
    (patrón Strategy: ver AtaqueJefe). Es otro hijo de Enemigo: PantallaJuego lo trata como a
    cualquier enemigo y él sobrescribe los métodos gancho (actualizar, atacar, recibirDano...).
*/
public class EnemigoJefe extends Enemigo
{
    private static final int DURACION_MAXIMA_ESCUDO = 600; // 10 segundos
    private static final String MUSICA_JEFE = "musica_jefe.wav";
    private static final String MUSICA_NORMAL = "piano-loops.wav";
    private static final int VIDAS_DE_PREMIO = 2;

    private final TipoJefe tipo;
    private final String nombre;
    private final int vidaMaxima;
    private final float velocidadBase;
    private float[] umbrales;                                   // fracciones de vida donde cambia de fase
    private final List<List<AtaqueJefe>> ataquesFase = new ArrayList<>();
    private final List<List<AtaqueJefe>> ataquesEscudo = new ArrayList<>();

    private int fase = 0;
    private int escudoFrames = 0;
    private int escudoNumero = 0;
    private final List<Enemigo> apoyo = new ArrayList<>();      // enemigos invocados durante el escudo

    private int tiempo = 0;
    private int framesDestello = 0;     // se pinta rojizo un instante cuando recibe un golpe
    private int framesAparicion = 0;    // parpadea después de teletransportarse

    // Embestida: 0 = no, 1 = aviso, 2 = bajando, 3 = subiendo
    private int estadoEmbestida = 0;
    private int framesAvisoEmbestida = 0;
    private float xEmbestida = 0;
    private final BarraJefe barra = new BarraJefe(); // su barra de vida, arriba de la pantalla

    public EnemigoJefe(TipoJefe tipo, Texture tx)
    {
        super(0, 0, tipo.getVelocidad(), 0, tx, tipo.getVida(), tipo.getPuntos());
        this.tipo = tipo;
        this.nombre = tipo.getNombre();
        this.vidaMaxima = tipo.getVida();
        this.velocidadBase = tipo.getVelocidad();
        setTamano(tx.getWidth() * 4, tx.getHeight() * 4); // los sprites son pixel-art: se agrandan x4
        setFactorHitbox(0.85f);
        armarAtaques();
    }

    // ---------------- Cada jefe es una combinación de ataques ----------------
    private void armarAtaques()
    {
        Color verde = new Color(0.45f, 1f, 0.35f, 1f);
        Color verdeHacker = new Color(0.2f, 1f, 0.6f, 1f);
        Color naranja = new Color(1f, 0.7f, 0.2f, 1f);
        Color rojo = new Color(1f, 0.35f, 0.35f, 1f);

        switch (tipo)
        {
            case TOXIC:
                // Escupe veneno apuntado, invoca un Lag de vez en cuando y, a la mitad de vida, se pone un escudo "MUTE"
                umbrales = new float[] { 0.5f };
                fase(new AtaqueApuntado(130, 1, 4f, verde), new AtaqueInvocar(420, 600, Apoyo.LAG, 1));
                escudo(new AtaqueLluvia(40, 3.6f, verde), new AtaqueInvocar(60, 0, Apoyo.BOT, 2));
                fase(new AtaqueApuntado(110, 2, 4.2f, verde), new AtaqueLluvia(75, 3.4f, verde),
                        new AtaqueInvocar(420, 500, Apoyo.LAG, 1));
                break;

            case HACKER:
                // Se teletransporta soltando anillos de balas; en el escudo crea un "firewall" de rayos
                umbrales = new float[] { 0.5f };
                fase(new AtaqueApuntado(120, 2, 4.2f, verdeHacker), new AtaqueSalto(300, 8, verdeHacker));
                escudo(new AtaqueLaser(170, 2, false), new AtaqueInvocar(60, 0, Apoyo.SPAMMER, 1));
                fase(new AtaqueApuntado(100, 2, 4.6f, verdeHacker), new AtaqueSalto(260, 10, verdeHacker),
                        new AtaqueLaser(260, 1, false));
                break;

            case SWEATY:
                // Espirales de balas y embestidas; dos escudos (a 2/3 y a 1/3 de vida)
                umbrales = new float[] { 0.66f, 0.33f };
                fase(new AtaqueEspiral(15, 2, 2.8f, naranja), new AtaqueEmbestida(720));
                escudo(new AtaqueEspiral(14, 2, 3f, naranja), new AtaqueInvocar(60, 0, Apoyo.CHEATER, 1));
                fase(new AtaqueEspiral(14, 2, 3f, naranja), new AtaqueEmbestida(600), new AtaqueApuntado(170, 2, 4.2f, naranja));
                escudo(new AtaqueAnillo(160, 10, 3f, naranja), new AtaqueInvocar(60, 0, Apoyo.CHEATER, 1));
                fase(new AtaqueEspiral(13, 2, 3.2f, naranja), new AtaqueEmbestida(520), new AtaqueApuntado(140, 2, 4.4f, naranja));
                break;

            default: // ADMIN
                // El jefe final: rayos de "ban", anillos y apoyo; tres fases y dos escudos
                umbrales = new float[] { 0.66f, 0.33f };
                fase(new AtaqueLaser(200, 1, false), new AtaqueApuntado(130, 2, 4.4f, rojo));
                escudo(new AtaqueLaser(170, 1, false), new AtaqueLaser(240, 1, true),
                        new AtaqueInvocar(60, 0, Apoyo.SPAMMER, 1), new AtaqueInvocar(150, 0, Apoyo.LAG, 1));
                fase(new AtaqueLaser(190, 2, false), new AtaqueLaser(300, 1, true), new AtaqueAnillo(220, 10, 3f, rojo),
                        new AtaqueApuntado(130, 2, 4.5f, rojo));
                escudo(new AtaqueAnillo(180, 10, 3.2f, rojo), new AtaqueLaser(220, 1, false),
                        new AtaqueInvocar(60, 0, Apoyo.CHEATER, 1), new AtaqueInvocar(200, 0, Apoyo.SNIPER, 1));
                fase(new AtaqueLaser(200, 2, false), new AtaqueLaser(320, 1, true), new AtaqueEspiral(16, 2, 3f, rojo),
                        new AtaqueApuntado(130, 2, 4.6f, rojo), new AtaqueAnillo(260, 10, 3.1f, rojo));
                break;
        }
    }

    private void fase(AtaqueJefe... ataques)
    {
        List<AtaqueJefe> lista = new ArrayList<>();
        for (AtaqueJefe a : ataques)
        {
            lista.add(a);
        }
        ataquesFase.add(lista);
    }

    private void escudo(AtaqueJefe... ataques)
    {
        List<AtaqueJefe> lista = new ArrayList<>();
        for (AtaqueJefe a : ataques)
        {
            lista.add(a);
        }
        ataquesEscudo.add(lista);
    }

    @Override
    public String getNombre()
    {
        return nombre;
    }

    public int getVidaMaxima()
    {
        return vidaMaxima;
    }

    // Devuelve una COPIA: así nadie puede cambiar desde afuera dónde cambia de fase el jefe
    public float[] getUmbrales()
    {
        return umbrales.clone();
    }

    public boolean tieneEscudo()
    {
        return escudoFrames > 0;
    }

    // Después de la primera fase el jefe está "enojado": se mueve más rápido
    public boolean estaEnojado()
    {
        return fase > 0;
    }

    // El jefe anota a sus enemigos de apoyo para saber cuándo los derrotaron a todos
    public void registrarApoyo(Enemigo enemigo)
    {
        apoyo.add(enemigo);
    }

    // Con escudo no recibe daño
    @Override
    public void recibirDano(float danio)
    {
        if (tieneEscudo())
        {
            return;
        }
        super.recibirDano(danio);
        framesDestello = 6;
    }

    // ---------------- Movimiento ----------------
    @Override
    public void actualizar()
    {
        tiempo++;
        if (framesDestello > 0)
        {
            framesDestello--;
        }
        if (framesAparicion > 0)
        {
            framesAparicion--;
        }

        if (isEntrando())
        {
            moverConRebote(); // baja desde arriba como los demás
            return;
        }
        if (estadoEmbestida != 0)
        {
            moverEmbestida();
            return;
        }

        // Patrulla de lado a lado; en cada fase nueva se mueve más rápido
        float rapidez = velocidadBase * (1f + 0.25f * fase);
        float direccion = getXVel() >= 0 ? 1 : -1;
        setXVel(direccion * rapidez);

        float x = getX() + getXVel();
        if (x < 0)
        {
            x = 0;
            setXVel(rapidez);
        }
        else if (x + getAncho() > Constantes.ANCHO)
        {
            x = Constantes.ANCHO - getAncho();
            setXVel(-rapidez);
        }
        setPosicion(x, getY() + (alturaPatrulla() - getY()) * 0.08f);
    }

    // Altura a la que flota: baja suavemente hasta ahí y se mece un poquito
    private float alturaPatrulla()
    {
        return Constantes.ALTO - getAlto() - 80 + (float) Math.sin(tiempo / 25.0) * 10;
    }

    private void moverEmbestida()
    {
        if (estadoEmbestida == 1)
        {
            // aviso: se pone sobre la nave y tiembla
            framesAvisoEmbestida--;
            float x = getX() + Math.max(-10, Math.min(10, xEmbestida - getAncho() / 2 - getX()));
            setPosicion(x + (tiempo % 4 < 2 ? -2 : 2), getY());
            if (framesAvisoEmbestida <= 0)
            {
                estadoEmbestida = 2;
            }
        }
        else if (estadoEmbestida == 2)
        {
            setPosicion(getX(), getY() - 18);
            if (getY() <= 50)
            {
                estadoEmbestida = 3;
            }
        }
        else
        {
            setPosicion(getX(), getY() + 5);
            if (getY() >= alturaPatrulla() - 5)
            {
                estadoEmbestida = 0;
            }
        }
    }

    // Lo llama el ataque AtaqueEmbestida
    public void iniciarEmbestida(float xObjetivo)
    {
        if (estadoEmbestida == 0 && !tieneEscudo())
        {
            estadoEmbestida = 1;
            framesAvisoEmbestida = 70;
            xEmbestida = xObjetivo;
        }
    }

    // Lo llama el ataque AtaqueSalto
    public void teletransportar(float x)
    {
        setPosicion(x, getY());
        framesAparicion = 40;
    }

    // ---------------- Ataques y fases ----------------
    @Override
    public void atacar(ContextoCombate contexto)
    {
        if (isEntrando())
        {
            return;
        }

        // ¿Le bajó la vida hasta el siguiente umbral? Entonces cambia de fase con un escudo
        if (fase < umbrales.length && getVida() / vidaMaxima <= umbrales[fase])
        {
            iniciarEscudo(contexto);
        }

        if (escudoFrames > 0)
        {
            escudoFrames--;
            for (AtaqueJefe a : ataquesEscudo.get(escudoNumero))
            {
                a.actualizar(this, contexto);
            }
            if (escudoFrames == 0 || apoyoDerrotado())
            {
                romperEscudo(contexto);
            }
            return;
        }

        for (AtaqueJefe a : ataquesFase.get(fase))
        {
            a.actualizar(this, contexto);
        }
    }

    private void iniciarEscudo(ContextoCombate contexto)
    {
        escudoNumero = fase;
        fase++;
        escudoFrames = DURACION_MAXIMA_ESCUDO;
        estadoEmbestida = 0;
        apoyo.clear();
        contexto.limpiarBalasEnemigas(); // respiro para el jugador
        contexto.anunciar("¡" + nombre + " SE PONE UN ESCUDO!");
        contexto.pedirPremio(getX() + getAncho() / 2 - 24, getY()); // al empezar el escudo también cae un premio para recuperarte
        contexto.reproducir("jefe_aviso.wav");
    }

    private void romperEscudo(ContextoCombate contexto)
    {
        escudoFrames = 0;
        framesDestello = 20;
        contexto.limpiarBalasEnemigas();
        contexto.anunciar("¡ESCUDO ROTO! FASE " + (fase + 1));
        contexto.pedirPremio(getX() + getAncho() / 2 - 24, getY()); // romper el escudo da un power-up
        contexto.reproducir("explosion.ogg");
    }

    // ¿Ya se derrotó a todos los enemigos que invocó durante el escudo?
    private boolean apoyoDerrotado()
    {
        if (apoyo.isEmpty())
        {
            return false;
        }
        for (Enemigo e : apoyo)
        {
            if (!e.estaMuerto())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Color getColorExplosion()
    {
        return new Color(1f, 1f, 0.8f, 1f);
    }

    @Override
    public boolean empujable()
    {
        return false;
    }

    @Override
    public boolean seDestruyeAlChocar()
    {
        return false;
    }

    @Override
    public boolean sueltaPowerUpSeguro()
    {
        return true;
    }

    // Al aparecer, el jefe se anuncia y pone su propia música
    @Override
    public void alAparecer(EfectosCombate efectos)
    {
        efectos.anunciar("¡LLEGÓ EL JEFE!");
        efectos.reproducirSonido("jefe_aviso.wav");
        efectos.cambiarMusica(MUSICA_JEFE);
    }

    // Al derrotarlo: explosión enorme, pantalla que tiembla, se limpian sus balas y el jugador gana vidas
    @Override
    public void alSerDerrotado(EfectosCombate efectos)
    {
        efectos.anunciar("¡JEFE ELIMINADO! +" + VIDAS_DE_PREMIO + " VIDAS");
        efectos.curarNave(VIDAS_DE_PREMIO);
        efectos.explotar(getX() + getAncho() / 2, getY() + getAlto() / 2, getColorExplosion(), 90, 11f);
        efectos.temblar(40, 12);
        efectos.destellar(Color.WHITE, 30);
        efectos.congelar(25);
        efectos.limpiarAtaquesEnemigos();
        efectos.cambiarMusica(MUSICA_NORMAL);
    }

    // Mientras el jefe pelea, sus refuerzos siguen entrando: no cuenta para los grupos
    @Override
    public boolean cuentaParaGrupo()
    {
        return false;
    }

    // Su barra de vida se dibuja arriba de la pantalla (solo cuando ya entró)
    @Override
    public void dibujarInterfaz(SpriteBatch batch, BitmapFont font)
    {
        if (!isEntrando())
        {
            barra.dibujar(batch, font, this);
        }
    }

    // Dibujo: rojizo al recibir un golpe o al avisar la embestida, parpadea al teletransportarse, burbuja con escudo
    @Override
    public void dibujar(SpriteBatch batch)
    {
        boolean avisando = estadoEmbestida == 1;
        if (framesDestello > 0 || avisando)
        {
            setTinte(1f, 0.45f, 0.45f, 1f);
        }
        if (framesAparicion > 0 && (framesAparicion / 4) % 2 == 0)
        {
            setTinte(1f, 1f, 1f, 0.35f);
        }
        super.dibujar(batch);
        setTinte(1f, 1f, 1f, 1f);

        if (tieneEscudo())
        {
            Texture halo = GestorRecursos.getInstance().getTextura("escudo.png");
            float latido = 0.55f + 0.25f * (float) Math.sin(tiempo / 8.0);
            batch.setColor(1f, 1f, 1f, latido);
            batch.draw(halo, getX() - 30, getY() - 30, getAncho() + 60, getAlto() + 60);
            batch.setColor(Color.WHITE);
        }
    }
}
