package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.util.GestorRecursos;

/*
    Pantalla de fin de partida. Hereda de PantallaBase (Template Method).
    Muestra el puntaje, avisa si se superó el récord (que se guarda en un archivo)
    y se despide con una frase al azar.
*/
public class PantallaGameOver extends PantallaBase
{
	private static final String[] FRASES = {
		"GG. Skill issue.",
		"Eso fue lag, lo juro.",
		"Alt+F4 no te salva.",
		"Te mató un bot... piénsalo.",
		"El chat dice: noob.",
		"Culpa del ping, siempre el ping."
	};

	private int score;
	private boolean nuevoRecord;
	private String frase = FRASES[MathUtils.random(FRASES.length - 1)];

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
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		font.getData().setScale(3.5f);
		font.setColor(1f, 0.3f, 0.3f, 1f);
		font.draw(batch, "GAME OVER", 0, 600, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		font.getData().setScale(2f);
		font.draw(batch, frase, 0, 510, Constantes.ANCHO, Align.center, false);

		font.getData().setScale(1.5f);
		font.draw(batch, "Puntaje: " + score, 0, 420, Constantes.ANCHO, Align.center, false);
		if (nuevoRecord)
		{
			font.setColor(1f, 0.85f, 0.2f, 1f);
			font.draw(batch, "¡NUEVO RÉCORD!", 0, 370, Constantes.ANCHO, Align.center, false);
			font.setColor(Color.WHITE);
		}
		else
		{
			font.draw(batch, "Récord: " + GestorRecursos.getInstance().getRecord(), 0, 370,
					Constantes.ANCHO, Align.center, false);
		}

		font.getData().setScale(1.5f);
		font.draw(batch, "Presiona F para presentar tus respetos", 0, 250, Constantes.ANCHO, Align.center, false);
		font.getData().setScale(1.5f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
				|| Gdx.input.isTouched())
		{
			game.setScreen(new PantallaSeleccion(game));
		}
	}

	// Suena la canción del menú (si ya está sonando no se reinicia)
	@Override
	public void show()
	{
		GestorRecursos.getInstance().reproducirMusica("musica_menu.wav");
	}
}
