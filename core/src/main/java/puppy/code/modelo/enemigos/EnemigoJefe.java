package puppy.code.modelo.enemigos;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.Constantes;
import puppy.code.modelo.ContextoCombate;
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
    private static final int DURACION_MAXIMA_ESCUDO = 900; // 15 segundos

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
                // Escupe veneno apuntado, invoca Lags y, a la mitad de vida, se pone un escudo "MUTE"
                umbrales = new float[] { 0.5f };
                fase(new AtaqueApuntado(100, 1, 4.5f, verde), new AtaqueInvocar(300, 420, Apoyo.LAG, 2));
                escudo(new AtaqueLluvia(18, 4.5f, verde), new AtaqueInvocar(60, 0, Apoyo.BOT, 3));
                fase(new AtaqueApuntado(70, 3, 4.8f, verde), new AtaqueLluvia(45, 4f, verde),
                        new AtaqueInvocar(240, 360, Apoyo.LAG, 2));
                break;

            case HACKER:
                // Se teletransporta soltando anillos de balas; en el escudo crea un "firewall" de rayos
                umbrales = new float[] { 0.5f };
                fase(new AtaqueApuntado(80, 3, 5f, verdeHacker), new AtaqueSalto(240, 10, verdeHacker));
                escudo(new AtaqueLaser(100, 3, false), new AtaqueInvocar(60, 0, Apoyo.SPAMMER, 2));
                fase(new AtaqueApuntado(60, 3, 5.5f, verdeHacker), new AtaqueSalto(180, 14, verdeHacker),
                        new AtaqueLaser(180, 2, false));
                break;

            case SWEATY:
                // Espirales de balas y embestidas; dos escudos (a 2/3 y a 1/3 de vida)
                umbrales = new float[] { 0.66f, 0.33f };
                fase(new AtaqueEspiral(7, 2, 3.2f, naranja), new AtaqueEmbestida(420));
                escudo(new AtaqueEspiral(6, 3, 3.5f, naranja), new AtaqueInvocar(60, 0, Apoyo.CHEATER, 2));
                fase(new AtaqueEspiral(6, 2, 3.8f, naranja), new AtaqueEmbestida(300), new AtaqueApuntado(110, 3, 5f, naranja));
                escudo(new AtaqueAnillo(80, 16, 3.8f, naranja), new AtaqueInvocar(60, 0, Apoyo.CHEATER, 2));
                fase(new AtaqueEspiral(5, 3, 4f, naranja), new AtaqueEmbestida(240), new AtaqueApuntado(80, 5, 5.2f, naranja));
                break;

            default: // ADMIN
                // El jefe final: rayos de "ban", anillos y apoyo; tres fases y dos escudos
                umbrales = new float[] { 0.66f, 0.33f };
                fase(new AtaqueLaser(150, 2, false), new AtaqueApuntado(90, 3, 5f, rojo));
                escudo(new AtaqueLaser(100, 2, false), new AtaqueLaser(140, 1, true),
                        new AtaqueInvocar(60, 0, Apoyo.SPAMMER, 2), new AtaqueInvocar(150, 0, Apoyo.LAG, 2));
                fase(new AtaqueLaser(120, 2, false), new AtaqueLaser(200, 1, true), new AtaqueAnillo(150, 14, 3.5f, rojo),
                        new AtaqueApuntado(90, 3, 5.2f, rojo));
                escudo(new AtaqueAnillo(80, 16, 3.8f, rojo), new AtaqueLaser(90, 2, false),
                        new AtaqueInvocar(60, 0, Apoyo.CHEATER, 2), new AtaqueInvocar(200, 0, Apoyo.SNIPER, 1));
                fase(new AtaqueLaser(90, 3, false), new AtaqueLaser(150, 2, true), new AtaqueEspiral(8, 2, 3.5f, rojo),
                        new AtaqueApuntado(70, 5, 5.5f, rojo), new AtaqueAnillo(140, 18, 3.6f, rojo));
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

    public float[] getUmbrales()
    {
        return umbrales;
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
            framesAvisoEmbestida = 50;
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
        contexto.getBalasEnemigas().clear(); // respiro para el jugador
        contexto.anunciar("¡" + nombre + " SE PONE UN ESCUDO!");
        contexto.reproducir("jefe_aviso.wav");
    }

    private void romperEscudo(ContextoCombate contexto)
    {
        escudoFrames = 0;
        framesDestello = 20;
        contexto.getBalasEnemigas().clear();
        contexto.anunciar("¡ESCUDO ROTO! FASE " + (fase + 1));
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
