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
    qué dibuja y a qué pantalla pasa.

    Es un menú de botones que se maneja con el mouse y con el teclado (ver MenuBotones).

    Tiene un secreto: si el jugador escribe el código Konami activa el MODO DIOS.
*/
public class PantallaMenu extends PantallaBase
{
	private static final int JUGAR = 0;
	private static final int GUIA = 1;
	private static final int OPCIONES = 2;
	private static final int SALIR = 3;

	private CodigoKonami konami = new CodigoKonami();
	private int tiempoMensaje = 0;  // cuánto queda mostrando el aviso del modo dios
	private int eleccion = -1;      // botón que se accionó en este fotograma
	private MenuBotones botones = new MenuBotones(new String[] { "JUGAR", "CÓMO SOBREVIVIR", "OPCIONES", "SALIR" },
			Constantes.ANCHO / 2f - 190, 390, 380, 60, 18, false);

	public PantallaMenu(SpaceNavigation game)
	{
		super(game);
	}

	@Override
	protected void actualizar(float delta)
	{
		if (tiempoMensaje > 0)
		{
			tiempoMensaje--;
		}

		// Easter egg: el código Konami activa el modo dios (más vidas)
		if (konami.actualizar())
		{
			game.setModoDios(true);
			tiempoMensaje = 240;
			GestorRecursos.getInstance().reproducirSonido("explosion.ogg");
		}

		eleccion = botones.actualizar(viewport);
		if (Gdx.input.isKeyJustPressed(Input.Keys.H))
		{
			eleccion = GUIA; // atajo de teclado
		}
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		// Título
		font.getData().setScale(4f);
		font.setColor(1f, 0.85f, 0.2f, 1f);
		font.draw(batch, "CALYPSO", 0, 740, Constantes.ANCHO, Align.center, false);

		font.getData().setScale(2f);
		font.setColor(0.4f, 0.9f, 1f, 1f);
		font.draw(batch, "~ Skill Issue Edition ~", 0, 640, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		font.getData().setScale(1.5f);
		font.draw(batch, "Récord actual: " + game.getHighScore(), 0, 570, Constantes.ANCHO, Align.center, false);

		botones.dibujar(batch, font);

		if (tiempoMensaje > 0)
		{
			font.getData().setScale(2f);
			font.setColor(0.3f, 1f, 0.4f, 1f);
			font.draw(batch, "¡MODO DIOS ACTIVADO!", 0, 150, Constantes.ANCHO, Align.center, false);
			font.setColor(Color.WHITE);
		}

		font.getData().setScale(1f);
		font.draw(batch, "Controles: flechas = moverte  |  ESPACIO = disparar  |  X = habilidad  |  P o ESC = pausa", 0, 70,
				Constantes.ANCHO, Align.center, false);
		font.draw(batch, "Los gamers de la vieja escuela conocen un truco secreto...", 0, 38,
				Constantes.ANCHO, Align.center, false);
		font.getData().setScale(1.5f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (eleccion == JUGAR)
		{
			// La primera vez se muestra la guía; después se va directo a elegir personaje
			if (game.isGuiaVista())
			{
				game.setScreen(new PantallaSeleccion(game));
			}
			else
			{
				game.setScreen(new PantallaGuia(game));
			}
		}
		else if (eleccion == GUIA)
		{
			game.setScreen(new PantallaGuia(game));
		}
		else if (eleccion == OPCIONES)
		{
			game.setScreen(new PantallaOpciones(game, this));
		}
		else if (eleccion == SALIR)
		{
			Gdx.app.exit();
		}
	}

	// Suena la canción del menú (si ya está sonando no se reinicia)
	@Override
	public void show()
	{
		GestorRecursos.getInstance().reproducirMusica("musica_menu.wav");
	}
}
