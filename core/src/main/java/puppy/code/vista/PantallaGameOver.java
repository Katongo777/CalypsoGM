package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import puppy.code.SpaceNavigation;

/*
    Pantalla de fin de partida. Hereda de PantallaBase (Template Method).
*/
public class PantallaGameOver extends PantallaBase
{
	public PantallaGameOver(SpaceNavigation game)
	{
		super(game);
	}

	@Override
	protected void actualizar(float delta)
	{
		// No hay lógica que actualizar
	}

	@Override
	protected void dibujar()
	{
		game.getFont().draw(game.getBatch(), "Game Over !!! ", 120, 400, 400, 1, true);
		game.getFont().draw(game.getBatch(), "Pincha en cualquier lado para reiniciar ...", 100, 300);
	}

	@Override
	protected void revisarTransicion()
	{
		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))
		{
			game.setScreen(new PantallaJuego(game, 1, 3, 0, 1, 1, 10));
		}
	}
}
