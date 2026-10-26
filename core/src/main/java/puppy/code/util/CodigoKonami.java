package puppy.code.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/*
    El famoso código Konami: arriba, arriba, abajo, abajo, izquierda, derecha,
    izquierda, derecha, B, A. Un "easter egg" clásico de los videojuegos.

    La clase recuerda en qué parte de la secuencia va el jugador. Si se equivoca
    de tecla, empieza de nuevo. Cuando completa las 10 teclas, avisa.
*/
public class CodigoKonami
{
    private static final int[] SECUENCIA = {
        Input.Keys.UP, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.DOWN,
        Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.LEFT, Input.Keys.RIGHT,
        Input.Keys.B, Input.Keys.A
    };

    private int posicion = 0;

    /*
        Se llama una vez por fotograma. Revisa si se presionó alguna de las teclas
        del código y devuelve true en el momento en que se completa la secuencia.
    */
    public boolean actualizar()
    {
        for (int tecla : new int[] { Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,
                                     Input.Keys.A, Input.Keys.B })
        {
            if (Gdx.input.isKeyJustPressed(tecla))
            {
                if (tecla == SECUENCIA[posicion])
                {
                    posicion++;
                }
                else
                {
                    // Se equivocó: vuelve a empezar (si justo apretó la primera tecla, ya cuenta)
                    posicion = (tecla == SECUENCIA[0]) ? 1 : 0;
                }

                if (posicion == SECUENCIA.length)
                {
                    posicion = 0;
                    return true;
                }
            }
        }
        return false;
    }
}
