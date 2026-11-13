package puppy.code.modelo;

import java.util.ArrayList;
import java.util.List;

import puppy.code.modelo.enemigos.Enemigo;

/*
    Lo que un enemigo necesita saber y poder hacer cuando ataca: dónde está la nave
    (para apuntarle), en qué oleada y dificultad estamos, y dónde dejar las balas, los
    rayos y los enemigos nuevos que cree.

    Así los enemigos no necesitan conocer a PantallaJuego: la pantalla les pasa este
    "contexto" y después recoge lo que dejaron (menos dependencias entre clases).

    ENCAPSULAMIENTO: el contexto nunca entrega sus listas. Funciona como un buzón:
    los enemigos y las habilidades DEJAN cosas (disparar, agregarRayo, limpiarBalasEnemigas)
    y la pantalla las RECOGE una vez por fotograma (recogerBalasEnemigas...), recibiendo
    una copia. Así nadie puede modificar el estado interno desde afuera.
*/
public class ContextoCombate
{
    private final Nave4 nave;
    private final Dificultad dificultad;
    private final int numeroOleada;
    private final List<BalaEnemiga> balasEnemigas = new ArrayList<>();
    private final List<Bullet> balasJugador = new ArrayList<>();
    private final List<Rayo> rayos = new ArrayList<>();
    private final List<Enemigo> nuevosEnemigos = new ArrayList<>();
    private final List<String> mensajes = new ArrayList<>();
    private final List<String> sonidos = new ArrayList<>();
    private final List<float[]> premios = new ArrayList<>();
    private boolean limpiarPedido = false;

    public ContextoCombate(Nave4 nave, Dificultad dificultad, int numeroOleada)
    {
        this.nave = nave;
        this.dificultad = dificultad;
        this.numeroOleada = numeroOleada;
    }

    // Un enemigo deja una bala disparada
    // La dificultad también cambia la velocidad de todas las balas enemigas (en Fácil son más lentas)
    public void disparar(BalaEnemiga bala)
    {
        bala.setXVel(bala.getXVel() * dificultad.getMultBalas());
        bala.setYVel(bala.getYVel() * dificultad.getMultBalas());
        balasEnemigas.add(bala);
    }

    public void agregarRayo(Rayo rayo)
    {
        rayos.add(rayo);
    }

    // Una habilidad deja una bala del jugador (por ejemplo, el Tiro Certero)
    public void agregarBalaJugador(Bullet bala)
    {
        balasJugador.add(bala);
    }

    // Pide borrar todas las balas enemigas (la Burbuja, o el jefe para darle un respiro al jugador)
    public void limpiarBalasEnemigas()
    {
        balasEnemigas.clear();
        limpiarPedido = true;
    }

    // La pantalla pregunta si alguien pidió limpiar; la respuesta se "consume" al leerla
    public boolean recogerPedidoLimpieza()
    {
        boolean pedido = limpiarPedido;
        limpiarPedido = false;
        return pedido;
    }

    public List<BalaEnemiga> recogerBalasEnemigas()
    {
        List<BalaEnemiga> copia = new ArrayList<>(balasEnemigas);
        balasEnemigas.clear();
        return copia;
    }

    public List<Bullet> recogerBalasJugador()
    {
        List<Bullet> copia = new ArrayList<>(balasJugador);
        balasJugador.clear();
        return copia;
    }

    public List<Rayo> recogerRayos()
    {
        List<Rayo> copia = new ArrayList<>(rayos);
        rayos.clear();
        return copia;
    }

    // Un enemigo (el jefe) crea enemigos nuevos
    public void agregarEnemigo(Enemigo enemigo)
    {
        nuevosEnemigos.add(enemigo);
    }

    // Un mensaje para el locutor ("¡ESCUDO!") y un sonido para que lo reproduzca la pantalla
    public void anunciar(String texto)
    {
        mensajes.add(texto);
    }

    public void reproducir(String sonido)
    {
        sonidos.add(sonido);
    }

    // Devuelve los enemigos nuevos y vacía la lista (la pantalla los recoge una vez por fotograma)
    public List<Enemigo> recogerNuevos()
    {
        List<Enemigo> copia = new ArrayList<>(nuevosEnemigos);
        nuevosEnemigos.clear();
        return copia;
    }

    public List<String> recogerMensajes()
    {
        List<String> copia = new ArrayList<>(mensajes);
        mensajes.clear();
        return copia;
    }

    public List<String> recogerSonidos()
    {
        List<String> copia = new ArrayList<>(sonidos);
        sonidos.clear();
        return copia;
    }

    // Tiempo entre disparos ajustado por la dificultad (los valores ya vienen en fotogramas)
    // Tiempo de aviso de un rayo ajustado por la dificultad (en Fácil hay más tiempo para esquivar)
    public int avisoRayo(int fotogramas)
    {
        return Math.round(fotogramas * dificultad.getMultAviso());
    }

    // El jefe pide un premio (un power-up) en una posición, por ejemplo al romperse su escudo
    public void pedirPremio(float x, float y)
    {
        premios.add(new float[] { x, y });
    }

    public List<float[]> recogerPremios()
    {
        List<float[]> copia = new ArrayList<>(premios);
        premios.clear();
        return copia;
    }

    public int intervalo(int fotogramas)
    {
        return Math.max(30, Math.round(fotogramas * dificultad.getMultIntervalo()));
    }

    // Igual, pero para ataques muy rápidos (como la espiral), que pueden ser de pocos fotogramas
    public int intervaloRapido(int fotogramas)
    {
        return Math.max(3, Math.round(fotogramas * dificultad.getMultIntervalo()));
    }

    public float getNaveCentroX() { return nave.getX() + nave.getAncho() / 2; }
    public float getNaveCentroY() { return nave.getY() + nave.getAlto() / 2; }
    public Nave4 getNave() { return nave; }
    public int getNumeroOleada() { return numeroOleada; }
}
