package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.util.CodigoKonami;
import puppy.code.util.GestorRecursos;

/*
    Pantalla de inicio. Hereda de PantallaBase (Template Method): solo define
    qué dibuja y cuándo pasa a la selección de personaje.

    Tiene un secreto: si el jugador escribe el código Konami activa el MODO DIOS.
*/
public class PantallaMenu extends PantallaBase
{
	private CodigoKonami konami = new CodigoKonami();
	private float tiempo = 0;       // para que el cartel "ENTER" parpadee
	private int tiempoMensaje = 0;  // cuánto queda mostrando el aviso del modo dios

	public PantallaMenu(SpaceNavigation game)
	{
		super(game);
	}

	@Override
	protected void actualizar(float delta)
	{
		tiempo += delta;
		if (tiempoMensaje > 0)
		{
			tiempoMensaje--;
		}

		// Easter egg: el código Konami activa el modo dios (más vidas)
		if (konami.actualizar())
		{
			game.setModoDios(true);
			tiempoMensaje = 240;
			GestorRecursos.getInstance().getSonido("explosion.ogg").play();
		}
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		// Título
		font.getData().setScale(6f);
		font.setColor(1f, 0.85f, 0.2f, 1f);
		font.draw(batch, "CALYPSO", 0, 620, Constantes.ANCHO, Align.center, false);

		font.getData().setScale(2.5f);
		font.setColor(0.4f, 0.9f, 1f, 1f);
		font.draw(batch, "~ Skill Issue Edition ~", 0, 530, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		font.getData().setScale(2f);
		font.draw(batch, "Récord actual: " + game.getHighScore(), 0, 440, Constantes.ANCHO, Align.center, false);

		// Cartel que parpadea suavemente
		float brillo = 0.65f + 0.35f * (float) Math.sin(tiempo * 4);
		font.setColor(1f, 1f, 1f, brillo);
		font.getData().setScale(2.5f);
		font.draw(batch, "Presiona ENTER para unirte a la partida", 0, 350, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		if (tiempoMensaje > 0)
		{
			font.getData().setScale(3f);
			font.setColor(0.3f, 1f, 0.4f, 1f);
			font.draw(batch, "¡MODO DIOS ACTIVADO!", 0, 260, Constantes.ANCHO, Align.center, false);
			font.setColor(Color.WHITE);
		}

		font.getData().setScale(1.5f);
		font.draw(batch, "Controles: flechas = moverte  |  ESPACIO = disparar  |  P = pausa", 0, 110,
				Constantes.ANCHO, Align.center, false);
		font.draw(batch, "Los gamers de la vieja escuela conocen un truco secreto...", 0, 70,
				Constantes.ANCHO, Align.center, false);
		font.getData().setScale(2f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isTouched())
		{
			game.setScreen(new PantallaSeleccion(game));
		}
	}
}
