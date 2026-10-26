package puppy.code.modelo;

import java.util.ArrayList;
import java.util.List;

import puppy.code.modelo.enemigos.Enemigo;

/*
    Lo que un enemigo necesita saber y poder hacer cuando ataca: dónde está la nave
    (para apuntarle), en qué oleada y dificultad estamos, y dónde dejar las balas y los
    enemigos nuevos que cree.

    Así los enemigos no necesitan conocer a PantallaJuego: la pantalla les pasa este
    "contexto" y después recoge lo que dejaron (menos dependencias entre clases).
*/
public class ContextoCombate
{
    private final Nave4 nave;
    private final Dificultad dificultad;
    private final int numeroOleada;
    private final ArrayList<BalaEnemiga> balasEnemigas = new ArrayList<>();
    private final ArrayList<Bullet> balasJugador = new ArrayList<>();
    private final ArrayList<Enemigo> nuevosEnemigos = new ArrayList<>();

    public ContextoCombate(Nave4 nave, Dificultad dificultad, int numeroOleada)
    {
        this.nave = nave;
        this.dificultad = dificultad;
        this.numeroOleada = numeroOleada;
    }

    // Un enemigo deja una bala disparada
    public void disparar(BalaEnemiga bala)
    {
        balasEnemigas.add(bala);
    }

    // Un enemigo (el jefe) crea enemigos nuevos
    public void agregarEnemigo(Enemigo enemigo)
    {
        nuevosEnemigos.add(enemigo);
    }

    // Devuelve los enemigos nuevos y vacía la lista (la pantalla los recoge una vez por fotograma)
    public List<Enemigo> recogerNuevos()
    {
        List<Enemigo> copia = new ArrayList<>(nuevosEnemigos);
        nuevosEnemigos.clear();
        return copia;
    }

    // Tiempo entre disparos ajustado por la dificultad (los valores ya vienen en fotogramas)
    public int intervalo(int fotogramas)
    {
        return Math.max(30, Math.round(fotogramas * dificultad.getMultIntervalo()));
    }

    public float getNaveCentroX() { return nave.getX() + nave.getAncho() / 2; }
    public float getNaveCentroY() { return nave.getY() + nave.getAlto() / 2; }
    public Nave4 getNave() { return nave; }
    public int getNumeroOleada() { return numeroOleada; }
    public ArrayList<BalaEnemiga> getBalasEnemigas() { return balasEnemigas; }
    public ArrayList<Bullet> getBalasJugador() { return balasJugador; }
}
