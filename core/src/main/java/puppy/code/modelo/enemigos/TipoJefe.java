package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Color;

import puppy.code.modelo.jefes.AtaqueAnillo;
import puppy.code.modelo.jefes.AtaqueApuntado;
import puppy.code.modelo.jefes.AtaqueEmbestida;
import puppy.code.modelo.jefes.AtaqueEspiral;
import puppy.code.modelo.jefes.AtaqueInvocar;
import puppy.code.modelo.jefes.AtaqueInvocar.Apoyo;
import puppy.code.modelo.jefes.AtaqueLaser;
import puppy.code.modelo.jefes.AtaqueLluvia;
import puppy.code.modelo.jefes.AtaqueSalto;

/*
    Los jefes que existen en el juego. Es un enum: cada valor guarda sus propios datos
    (nombre, imagen, vida, puntos, velocidad y el aviso que se muestra antes de pelear)
    y, además, sabe armar sus propios ataques (método armarAtaques, distinto en cada uno).

    Así EnemigoJefe no necesita un switch con todos los jefes: le pide a su tipo que lo
    arme. Agregar un jefe nuevo es agregar un valor aquí (principio Abierto/Cerrado).
*/
public enum TipoJefe
{
    TOXIC("EL TOXIC", "jefe_toxic.png", 28, 500, 2f, "¡JEFE! Escupe veneno. A la mitad de su vida se pone un escudo")
    {
        @Override
        void armarAtaques(EnemigoJefe jefe)
        {
            // Escupe veneno apuntado, invoca un Lag de vez en cuando y, a la mitad de vida, se pone un escudo "MUTE"
            Color verde = new Color(0.45f, 1f, 0.35f, 1f);
            jefe.definirUmbrales(0.5f);
            jefe.fase(new AtaqueApuntado(130, 1, 4f, verde), new AtaqueInvocar(420, 600, Apoyo.LAG, 1));
            jefe.escudo(new AtaqueLluvia(40, 3.6f, verde), new AtaqueInvocar(60, 0, Apoyo.BOT, 2));
            jefe.fase(new AtaqueApuntado(110, 2, 4.2f, verde), new AtaqueLluvia(75, 3.4f, verde),
                    new AtaqueInvocar(420, 500, Apoyo.LAG, 1));
        }
    },
    HACKER("EL HACKER", "jefe_hacker.png", 36, 1000, 2.2f, "¡JEFE! Se teletransporta y lanza rayos. Mira las líneas rojas")
    {
        @Override
        void armarAtaques(EnemigoJefe jefe)
        {
            // Se teletransporta soltando anillos de balas; en el escudo crea un "firewall" de rayos
            Color verdeHacker = new Color(0.2f, 1f, 0.6f, 1f);
            jefe.definirUmbrales(0.5f);
            jefe.fase(new AtaqueApuntado(120, 2, 4.2f, verdeHacker), new AtaqueSalto(300, 8, verdeHacker));
            jefe.escudo(new AtaqueLaser(170, 2, false), new AtaqueInvocar(60, 0, Apoyo.SPAMMER, 1));
            jefe.fase(new AtaqueApuntado(100, 2, 4.6f, verdeHacker), new AtaqueSalto(260, 10, verdeHacker),
                    new AtaqueLaser(260, 1, false));
        }
    },
    SWEATY("EL SWEATY", "jefe_sweaty.png", 48, 1500, 2.6f, "¡JEFE! Espirales de balas y embestidas. Tiene 2 escudos")
    {
        @Override
        void armarAtaques(EnemigoJefe jefe)
        {
            // Espirales de balas y embestidas; dos escudos (a 2/3 y a 1/3 de vida)
            Color naranja = new Color(1f, 0.7f, 0.2f, 1f);
            jefe.definirUmbrales(0.66f, 0.33f);
            jefe.fase(new AtaqueEspiral(15, 2, 2.8f, naranja), new AtaqueEmbestida(720));
            jefe.escudo(new AtaqueEspiral(14, 2, 3f, naranja), new AtaqueInvocar(60, 0, Apoyo.CHEATER, 1));
            jefe.fase(new AtaqueEspiral(14, 2, 3f, naranja), new AtaqueEmbestida(600), new AtaqueApuntado(170, 2, 4.2f, naranja));
            jefe.escudo(new AtaqueAnillo(160, 10, 3f, naranja), new AtaqueInvocar(60, 0, Apoyo.CHEATER, 1));
            jefe.fase(new AtaqueEspiral(13, 2, 3.2f, naranja), new AtaqueEmbestida(520), new AtaqueApuntado(140, 2, 4.4f, naranja));
        }
    },
    ADMIN("EL ADMIN", "jefe_admin.png", 55, 3000, 2.4f, "¡JEFE! Rayos de ban en 3 fases. Rompe sus escudos")
    {
        @Override
        void armarAtaques(EnemigoJefe jefe)
        {
            // El jefe final: rayos de "ban", anillos y apoyo; tres fases y dos escudos
            Color rojo = new Color(1f, 0.35f, 0.35f, 1f);
            jefe.definirUmbrales(0.66f, 0.33f);
            jefe.fase(new AtaqueLaser(200, 1, false), new AtaqueApuntado(130, 2, 4.4f, rojo));
            jefe.escudo(new AtaqueLaser(170, 1, false), new AtaqueLaser(240, 1, true),
                    new AtaqueInvocar(60, 0, Apoyo.SPAMMER, 1), new AtaqueInvocar(150, 0, Apoyo.LAG, 1));
            jefe.fase(new AtaqueLaser(190, 2, false), new AtaqueLaser(300, 1, true), new AtaqueAnillo(220, 10, 3f, rojo),
                    new AtaqueApuntado(130, 2, 4.5f, rojo));
            jefe.escudo(new AtaqueAnillo(180, 10, 3.2f, rojo), new AtaqueLaser(220, 1, false),
                    new AtaqueInvocar(60, 0, Apoyo.CHEATER, 1), new AtaqueInvocar(200, 0, Apoyo.SNIPER, 1));
            jefe.fase(new AtaqueLaser(200, 2, false), new AtaqueLaser(320, 1, true), new AtaqueEspiral(16, 2, 3f, rojo),
                    new AtaqueApuntado(130, 2, 4.6f, rojo), new AtaqueAnillo(260, 10, 3.1f, rojo));
        }
    };

    private final String nombre;
    private final String archivoImagen;
    private final int vida;
    private final int puntos;
    private final float velocidad;
    private final String aviso;

    TipoJefe(String nombre, String archivoImagen, int vida, int puntos, float velocidad, String aviso)
    {
        this.nombre = nombre;
        this.archivoImagen = archivoImagen;
        this.vida = vida;
        this.puntos = puntos;
        this.velocidad = velocidad;
        this.aviso = aviso;
    }

    // Cada jefe define sus fases, sus escudos y qué ataques usa en cada uno (solo lo llama EnemigoJefe)
    abstract void armarAtaques(EnemigoJefe jefe);

    public String getNombre() { return nombre; }
    public String getArchivoImagen() { return archivoImagen; }
    public int getVida() { return vida; }
    public int getPuntos() { return puntos; }
    public float getVelocidad() { return velocidad; }
    public String getAviso() { return aviso; }
}
