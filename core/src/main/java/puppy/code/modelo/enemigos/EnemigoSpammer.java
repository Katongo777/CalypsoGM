package puppy.code.modelo.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.Constantes;
import puppy.code.modelo.BalaEnemiga;
import puppy.code.modelo.ContextoCombate;

/*
    El "Spammer" (aparece desde la oleada 6): un globo de chat que flota de lado a lado
    mientras sube y baja un poquito, y dispara RÁFAGAS de 3 balas seguidas hacia abajo
    ("spamea el chat"). Tiene 1 de vida y da 20 puntos.

    Es otro hijo de Enemigo: sobrescribe actualizar() para moverse distinto y atacar()
    para disparar a su manera; la pantalla no necesita saber que existe.
*/
public class EnemigoSpammer extends Enemigo
{
    private final float alturaObjetivo = MathUtils.random(430, 650); // altura a la que se queda flotando
    private int tiempo = 0;
    private int framesParaRafaga = MathUtils.random(90, 200);
    private int balasPorDisparar = 0;   // cuántas balas faltan de la ráfaga actual
    private int framesEntreBalas = 0;

    public EnemigoSpammer(float x, float y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 1, 20);
        setTamano(56, 48);
        ubicarDentroDePantalla();
    }

    @Override
    public String getNombre()
    {
        return "Spammer";
    }

    @Override
    public void actualizar()
    {
        tiempo++;
        if (isEntrando())
        {
            moverConRebote(); // baja desde arriba como los demás
            return;
        }

        // De lado a lado, rebotando en las paredes
        float x = getX() + getXVel();
        if (x < 0)
        {
            x = 0;
            setXVel(Math.abs(getXVel()));
        }
        else if (x + getAncho() > Constantes.ANCHO)
        {
            x = Constantes.ANCHO - getAncho();
            setXVel(-Math.abs(getXVel()));
        }
        // Baja suavemente hasta su altura y flota con una ondita
        float y = getY() + (alturaObjetivo - getY()) * 0.04f + MathUtils.sin(tiempo / 15f) * 1.2f;
        setPosicion(x, y);
    }

    @Override
    public void atacar(ContextoCombate contexto)
    {
        if (isEntrando())
        {
            return;
        }
        if (balasPorDisparar > 0)
        {
            framesEntreBalas--;
            if (framesEntreBalas <= 0)
            {
                contexto.disparar(new BalaEnemiga(getX() + getAncho() / 2, getY() + 10, 0, -5f)
                        .conColor(new Color(0.5f, 0.9f, 1f, 1f)));
                balasPorDisparar--;
                framesEntreBalas = 10;
            }
            return;
        }
        framesParaRafaga--;
        if (framesParaRafaga <= 0)
        {
            framesParaRafaga = contexto.intervalo(Math.max(150, 260 - 5 * contexto.getNumeroOleada())) + MathUtils.random(0, 60);
            balasPorDisparar = 3;
            framesEntreBalas = 0;
        }
    }
}
