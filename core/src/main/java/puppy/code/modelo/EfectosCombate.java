package puppy.code.modelo;

import com.badlogic.gdx.graphics.Color;

/*
    Lo que un enemigo puede pedirle a la pantalla cuando aparece o cuando lo derrotan:
    anunciar algo, hacer temblar la pantalla, cambiar la música, curar a la nave...

    Es una INTERFAZ: el enemigo no sabe qué pantalla la implementa (hoy es PantallaJuego).
    Gracias a esto PantallaJuego no necesita preguntar "¿este enemigo es un jefe?": le avisa
    a cada enemigo que apareció o que murió, y cada uno decide qué efectos pide.
*/
public interface EfectosCombate
{
    // Un mensaje grande en pantalla (lo muestra el locutor)
    void anunciar(String texto);

    void reproducirSonido(String archivo);

    // Cambia la canción que suena (por ejemplo, la música del jefe)
    void cambiarMusica(String archivo);

    // Hace temblar la pantalla durante unos fotogramas
    void temblar(int fotogramas, float intensidad);

    // Pinta la pantalla de un color que se va desvaneciendo
    void destellar(Color color, int fotogramas);

    // Detiene el juego un instante para que el golpe "pese"
    void congelar(int fotogramas);

    // Partículas de explosión en un punto
    void explotar(float x, float y, Color color, int cantidad, float fuerza);

    // Borra todas las balas y rayos de los enemigos
    void limpiarAtaquesEnemigos();

    // Le devuelve vidas a la nave del jugador
    void curarNave(int vidas);
}
