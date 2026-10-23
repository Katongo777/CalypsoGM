package puppy.code.modelo.enemigos;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;

import puppy.code.util.GestorRecursos;

/*
    El "Cheater": grande, lento y resistente (3 de vida). Cuando por fin lo
    eliminas, se descubre el cheat y suelta 2 Lags.

    Para eso sobrescribe el método "gancho" alMorir() de Enemigo.
*/
public class EnemigoCheater extends Enemigo
{
    public EnemigoCheater(float x, float y, float xVel, float yVel, Texture tx)
    {
        super(x, y, xVel, yVel, tx, 3, 50); // 3 de vida, da 50 puntos
        setTamano(96, 96);
        ubicarDentroDePantalla();
    }

    @Override
    public String getNombre()
    {
        return "Cheater";
    }

    @Override
    public void actualizar()
    {
        moverConRebote();
    }

    // Al morir se parte en dos Lags que salen disparados en direcciones opuestas
    @Override
    public List<Enemigo> alMorir()
    {
        Texture txLag = GestorRecursos.getInstance().getTextura("lag.png");
        List<Enemigo> hijos = new ArrayList<>();
        hijos.add(new EnemigoLag(getX(), getY(), 3, 3, txLag));
        hijos.add(new EnemigoLag(getX(), getY(), -3, -3, txLag));
        return hijos;
    }
}
