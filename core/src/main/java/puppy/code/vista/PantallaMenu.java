package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import puppy.code.SpaceNavigation;

/*
    Pantalla de inicio. Hereda de PantallaBase (Template Method): solo define
    qué dibuja y cuándo pasa a la pantalla de juego.
*/
public class PantallaMenu extends PantallaBase
{
	public PantallaMenu(SpaceNavigation game)
	{
		super(game);
	}

	@Override
	protected void actualizar(float delta)
	{
		// El menú no tiene lógica que actualizar
	}

	@Override
	protected void dibujar()
	{
		game.getFont().draw(game.getBatch(), "Bienvenido a Space Navigation !", 140, 400);
		game.getFont().draw(game.getBatch(), "Pincha en cualquier lado o presiona cualquier tecla para comenzar ...", 100, 300);
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
