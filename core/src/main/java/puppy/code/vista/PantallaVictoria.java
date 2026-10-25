package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.util.GestorRecursos;

/*
    Pantalla de victoria: se llega aquí al derrotar al jefe final (oleada 10).
    Muestra el puntaje, avisa si hubo récord y deja caer confeti de colores.
    Hereda de PantallaBase (Template Method).
*/
public class PantallaVictoria extends PantallaBase
{
	private static final int CANTIDAD_CONFETI = 70;

	private int score;
	private boolean nuevoRecord;

	// Cada pedacito de confeti tiene posición, velocidad de caída y color
	private float[] confetiX = new float[CANTIDAD_CONFETI];
	private float[] confetiY = new float[CANTIDAD_CONFETI];
	private float[] confetiVel = new float[CANTIDAD_CONFETI];
	private Color[] confetiColor = new Color[CANTIDAD_CONFETI];

	public PantallaVictoria(SpaceNavigation game, int score, boolean nuevoRecord)
	{
		super(game);
		this.score = score;
		this.nuevoRecord = nuevoRecord;

		Color[] colores = { Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.WHITE };
		for (int i = 0; i < CANTIDAD_CONFETI; i++)
		{
			confetiX[i] = MathUtils.random(Constantes.ANCHO);
			confetiY[i] = MathUtils.random(Constantes.ALTO);
			confetiVel[i] = MathUtils.random(1.5f, 4f);
			confetiColor[i] = colores[MathUtils.random(colores.length - 1)];
		}
	}

	@Override
	protected void actualizar(float delta)
	{
		for (int i = 0; i < CANTIDAD_CONFETI; i++)
		{
			confetiY[i] -= confetiVel[i];
			if (confetiY[i] < -10)
			{
				confetiY[i] = Constantes.ALTO + 10; // vuelve a caer desde arriba
				confetiX[i] = MathUtils.random(Constantes.ANCHO);
			}
		}
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
		for (int i = 0; i < CANTIDAD_CONFETI; i++)
		{
			batch.setColor(confetiColor[i]);
			batch.draw(pixel, confetiX[i], confetiY[i], 8, 8);
		}
		batch.setColor(Color.WHITE);

		font.getData().setScale(4f);
		font.setColor(1f, 0.85f, 0.2f, 1f);
		font.draw(batch, "¡VICTORIA!", 0, 620, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		font.getData().setScale(2f);
		font.draw(batch, "Baneaste al Admin. GG EZ.", 0, 520, Constantes.ANCHO, Align.center, false);

		font.getData().setScale(1.5f);
		font.draw(batch, "Puntaje final: " + score, 0, 430, Constantes.ANCHO, Align.center, false);
		if (nuevoRecord)
		{
			font.setColor(1f, 0.85f, 0.2f, 1f);
			font.draw(batch, "¡NUEVO RÉCORD!", 0, 380, Constantes.ANCHO, Align.center, false);
			font.setColor(Color.WHITE);
		}
		else
		{
			font.draw(batch, "Récord: " + GestorRecursos.getInstance().getRecord(), 0, 380,
					Constantes.ANCHO, Align.center, false);
		}

		font.setColor(0.4f, 0.9f, 1f, 1f);
		font.draw(batch, "Presiona ENTER para jugar de nuevo", 0, 250, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);
	}

	@Override
	protected void revisarTransicion()
	{
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.F))
		{
			game.setScreen(new PantallaSeleccion(game));
		}
	}
}
