package puppy.code.modelo;

import com.badlogic.gdx.graphics.Color;

import puppy.code.Constantes;
import puppy.code.util.GestorRecursos;

/*
    Una bala disparada por un enemigo. Es hija de Entidad (igual que Bullet, la bala del
    jugador) pero hace daño a la NAVE en vez de a los enemigos.

    Se puede teñir de un color (el jefe Toxic dispara balas verdes, por ejemplo).
*/
public class BalaEnemiga extends Entidad
{
    private boolean destruida = false;

    public BalaEnemiga(float x, float y, float xVel, float yVel)
    {
        super(x - 12, y - 12, xVel, yVel, GestorRecursos.getInstance().getTextura("bala_enemiga.png"));
        setTamano(24, 24);
        setFactorHitbox(0.5f); // el centro de la bala: pegarle de refilón no cuenta
    }

    /*
        Crea una bala que sale de (x, y) apuntando hacia (objetivoX, objetivoY).
        "desvio" gira la dirección unos radianes (sirve para disparar en abanico).
    */
    public static BalaEnemiga apuntada(float x, float y, float objetivoX, float objetivoY, float rapidez, float desvio)
    {
        float angulo = (float) Math.atan2(objetivoY - y, objetivoX - x) + desvio;
        return new BalaEnemiga(x, y, (float) Math.cos(angulo) * rapidez, (float) Math.sin(angulo) * rapidez);
    }

    public BalaEnemiga conColor(Color color)
    {
        setTinte(color.r, color.g, color.b, color.a);
        return this;
    }

    @Override
    public void actualizar()
    {
        setPosicion(getX() + getXVel(), getY() + getYVel());
    }

    public boolean salioDePantalla()
    {
        return getX() < -40 || getX() > Constantes.ANCHO + 40 || getY() < -40 || getY() > Constantes.ALTO + 40;
    }

    public void destruir() { destruida = true; }
    public boolean isDestruida() { return destruida; }
}
