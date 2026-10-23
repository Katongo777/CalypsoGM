package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/*
    El "Lag": pequeño, rápido y molesto. Se dibuja como el circulito de "cargando..."
    que gira, y de vez en cuando pega un "lag spike": se teletransporta un poco.

    Sobrescribe actualizar() con su propio comportamiento (polimorfismo): la
    pantalla solo llama enemigo.actualizar() sin saber que este es distinto.
*/
public class EnemigoLag extends Enemigo
{
    private int framesParaSalto;

    public EnemigoLag(float x, float y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 1, 15); // 1 de vida, da 15 puntos
        setTamano(48, 48);
        ubicarDentroDePantalla();
        framesParaSalto = MathUtils.random(60, 150);
    }

    @Override
    public String getNombre()
    {
        return "Lag";
    }

    @Override
    public void actualizar()
    {
        moverConRebote();
        rotar(-8); // el spinner de carga gira

        framesParaSalto--;
        if (framesParaSalto <= 0)
        {
            // Lag spike: salta de golpe a otra posición cercana
            setPosicion(getX() + MathUtils.random(-120, 120), getY() + MathUtils.random(-120, 120));
            ubicarDentroDePantalla();
            framesParaSalto = MathUtils.random(60, 150);
        }
    }
}
