package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import puppy.code.SpaceNavigation;
import puppy.code.util.GestorRecursos;

/*
    Pantalla de fin de partida. Hereda de PantallaBase (Template Method).
    Muestra el puntaje y avisa si se superó el récord (que se guarda en un archivo).
*/
public class PantallaGameOver extends PantallaBase
{
	private int score;
	private boolean nuevoRecord;

	public PantallaGameOver(SpaceNavigation game, int score, boolean nuevoRecord)
	{
		super(game);
		this.score = score;
		this.nuevoRecord = nuevoRecord;
	}

	@Override
	protected void actualizar(float delta)
	{
		// No hay lógica que actualizar
	}

	@Override
	protected void dibujar()
	{
		game.getFont().draw(game.getBatch(), "Game Over !!! ", 120, 500, 400, 1, true);
		game.getFont().draw(game.getBatch(), "Puntaje: " + score, 100, 420);
		if (nuevoRecord)
		{
			game.getFont().draw(game.getBatch(), "¡NUEVO RÉCORD!", 100, 370);
		}
		else
		{
			game.getFont().draw(game.getBatch(), "Récord: " + GestorRecursos.getInstance().getRecord(), 100, 370);
		}
		game.getFont().draw(game.getBatch(), "Pincha en cualquier lado para elegir personaje de nuevo ...", 100, 300);
	}

	@Override
	protected void revisarTransicion()
	{
		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))
		{
			game.setScreen(new PantallaSeleccion(game));
		}
	}
}
