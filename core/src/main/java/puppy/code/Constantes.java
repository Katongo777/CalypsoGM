package puppy.code;

/*
    Valores fijos del juego en un solo lugar.

    Antes cada clase le preguntaba el tamaño a la ventana (Gdx.graphics.getWidth())
    y, si la ventana cambiaba de tamaño, el juego se descuadraba. Ahora el "mundo"
    del juego siempre mide lo mismo y la ventana solo lo escala (ver PantallaBase).
*/
public final class Constantes
{
    public static final int ANCHO = 1200;
    public static final int ALTO = 800;

    // Constructor privado: es una clase solo de constantes, no se instancia
    private Constantes() { }
}
