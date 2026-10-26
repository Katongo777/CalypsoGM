package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.modelo.Personaje;
import puppy.code.util.GestorRecursos;

/*
    Pantalla de selección de personaje. Hereda de PantallaBase (Template Method).
    El personaje elegido define la nave y el arma con la que empieza la partida.
*/
public class PantallaSeleccion extends PantallaBase
{
	private Personaje[] personajes = Personaje.values();
	private int seleccionado = 0;
	private Texture pixel = GestorRecursos.getInstance().getPixelBlanco();
	private boolean iniciar = false;   // se hizo clic en la tarjeta que ya estaba elegida
	private float ultimoMouseX = -1;
	private float ultimoMouseY = -1;

	public PantallaSeleccion(SpaceNavigation game)
	{
		super(game);
	}

	@Override
	protected void actualizar(float delta)
	{
		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
		{
			seleccionado = (seleccionado + personajes.length - 1) % personajes.length;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
		{
			seleccionado = (seleccionado + 1) % personajes.length;
		}

		// Con el mouse: al pasar por encima se elige la tarjeta y un clic en la elegida inicia la partida
		Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		boolean mouseSeMovio = mouse.x != ultimoMouseX || mouse.y != ultimoMouseY;
		ultimoMouseX = mouse.x;
		ultimoMouseY = mouse.y;
		for (int i = 0; i < personajes.length; i++)
		{
			if (new Rectangle(80 + i * 360, 170, 320, 430).contains(mouse))
			{
				if (mouseSeMovio)
				{
					seleccionado = i;
				}
				if (Gdx.input.justTouched())
				{
					iniciar = seleccionado == i;
					seleccionado = i;
				}
			}
		}
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		font.getData().setScale(2f);
		font.draw(batch, "ELIGE TU PERSONAJE", 0, Constantes.ALTO - 60, Constantes.ANCHO, Align.center, false);

		for (int i = 0; i < personajes.length; i++)
		{
			dibujarTarjeta(batch, font, personajes[i], 80 + i * 360, 170, i == seleccionado);
		}

		font.getData().setScale(1.5f);
		font.draw(batch, "Flechas o mouse para elegir   |   ENTER o clic para jugar   |   ESC: volver", 0, 110,
				Constantes.ANCHO, Align.center, false);
	}

	// Dibuja una tarjeta con la nave y los datos del personaje
	private void dibujarTarjeta(SpriteBatch batch, BitmapFont font, Personaje p, float x, float y, boolean activa)
	{
		float ancho = 320;
		float alto = 430;

		// Fondo de la tarjeta; la elegida es más clara y tiene borde amarillo
		if (activa)
		{
			batch.setColor(0.18f, 0.18f, 0.45f, 0.95f);
		}
		else
		{
			batch.setColor(0.08f, 0.08f, 0.2f, 0.8f);
		}
		batch.draw(pixel, x, y, ancho, alto);
		if (activa)
		{
			batch.setColor(1f, 0.85f, 0.2f, 1f);
			batch.draw(pixel, x, y, ancho, 4);
			batch.draw(pixel, x, y + alto - 4, ancho, 4);
			batch.draw(pixel, x, y, 4, alto);
			batch.draw(pixel, x + ancho - 4, y, 4, alto);
		}
		batch.setColor(Color.WHITE);

		// La nave (16x16 píxeles) dibujada 8 veces más grande
		Texture nave = GestorRecursos.getInstance().getTextura(p.getArchivoNave());
		batch.draw(nave, x + 96, y + 270, 128, 128);

		font.getData().setScale(1.5f);
		font.draw(batch, p.getNombre(), x, y + 250, ancho, Align.center, false);

		font.getData().setScale(1.5f);
		font.draw(batch, "Arma: " + p.crearArma().getNombre(), x, y + 195, ancho, Align.center, false);
		font.draw(batch, "Vidas: " + p.getVidas(), x, y + 155, ancho, Align.center, false);
		font.draw(batch, "Velocidad: " + (int) p.getVelocidad(), x, y + 115, ancho, Align.center, false);

		font.getData().setScale(1f);
		font.draw(batch, "\"" + p.getFrase() + "\"", x + 20, y + 70, ancho - 40, Align.center, true);
		font.getData().setScale(1.5f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || iniciar)
		{
			game.setScreen(PantallaJuego.nuevaPartida(game, personajes[seleccionado]));
		}
		else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
		{
			game.setScreen(new PantallaMenu(game));
		}
	}

	// Suena la canción del menú (si ya está sonando no se reinicia)
	@Override
	public void show()
	{
		GestorRecursos.getInstance().reproducirMusica("musica_menu.wav");
	}
}
