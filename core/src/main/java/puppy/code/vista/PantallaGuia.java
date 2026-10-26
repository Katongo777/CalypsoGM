package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.modelo.niveles.DirectorNiveles;
import puppy.code.util.GestorRecursos;

/*
    Pantalla "Cómo sobrevivir": explica los enemigos, los power-ups, los controles y
    cómo funciona la campaña. Se muestra automáticamente la primera vez que el jugador
    aprieta ENTER en el menú, y después se puede abrir con la tecla H.

    Hereda de PantallaBase (Template Method): solo define qué dibuja y a dónde va.
*/
public class PantallaGuia extends PantallaBase
{
	private static final float COLUMNA_ENEMIGOS = 50;
	private static final float COLUMNA_POWERUPS = 680;
	private static final float ANCHO_TEXTO = 440;
	private static final float ANCHO_TEXTO_POWERUPS = 400;

	private final MenuBotones botones = new MenuBotones(new String[] { "CONTINUAR", "VOLVER AL MENÚ" },
			Constantes.ANCHO / 2f - 330, 22, 320, 54, 20, true);
	private int eleccion = -1;

	public PantallaGuia(SpaceNavigation game)
	{
		super(game);
		game.setGuiaVista(true); // ya la vio: no se vuelve a mostrar sola
	}

	@Override
	protected void actualizar(float delta)
	{
		eleccion = botones.actualizar(viewport);
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();
		GestorRecursos recursos = GestorRecursos.getInstance();

		font.getData().setScale(3f);
		font.setColor(1f, 0.85f, 0.2f, 1f);
		font.draw(batch, "CÓMO SOBREVIVIR", 0, Constantes.ALTO - 15, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		// ---- Enemigos ----
		titulo(batch, font, "ENEMIGOS", COLUMNA_ENEMIGOS, 690);
		fila(batch, font, recursos.getTextura("bot.png"), "BOT", "Lento y fácil de eliminar. 1 vida.",
				COLUMNA_ENEMIGOS, 620, 80, ANCHO_TEXTO);
		fila(batch, font, recursos.getTextura("lag.png"), "LAG",
				"Se teletransporta, pero tiembla justo antes de saltar. 1 vida.", COLUMNA_ENEMIGOS, 510, 80, ANCHO_TEXTO);
		fila(batch, font, recursos.getTextura("cheater.png"), "CHEATER",
				"Grande y resistente: aguanta 3 balas y al morir suelta 2 Lags.", COLUMNA_ENEMIGOS, 400, 80, ANCHO_TEXTO);
		fila(batch, font, recursos.getTextura("jefe_toxic.png"), "JEFE",
				"Aparece en las oleadas 5 y 10. Tiene una barra de vida arriba e invoca Lags.", COLUMNA_ENEMIGOS, 290, 80, ANCHO_TEXTO);

		// ---- Power-ups ----
		titulo(batch, font, "POWER-UPS", COLUMNA_POWERUPS, 690);
		fila(batch, font, recursos.getTextura("pw_pizza.png"), "PIZZA", "+1 vida.", COLUMNA_POWERUPS, 630, 56, ANCHO_TEXTO_POWERUPS);
		fila(batch, font, recursos.getTextura("pw_silla.png"), "SILLA GAMER", "Escudo durante 10 segundos.",
				COLUMNA_POWERUPS, 550, 56, ANCHO_TEXTO_POWERUPS);
		fila(batch, font, recursos.getTextura("pw_rgb.png"), "RGB", "Disparo doble por 10 segundos.",
				COLUMNA_POWERUPS, 470, 56, ANCHO_TEXTO_POWERUPS);
		fila(batch, font, recursos.getTextura("pw_lootbox.png"), "LOOT BOX", "Te da un arma al azar por un rato.",
				COLUMNA_POWERUPS, 390, 56, ANCHO_TEXTO_POWERUPS);

		// ---- Campaña y controles ----
		titulo(batch, font, "LA CAMPAÑA", COLUMNA_POWERUPS, 290);
		font.getData().setScale(1f);
		font.draw(batch, "Son " + DirectorNiveles.TOTAL_OLEADAS + " oleadas. Cada tanto viene una HARDCORE, "
				+ "en la 5 está el jefe EL TOXIC y en la 10 el jefe final EL ADMIN. "
				+ "Si lo derrotas, ¡ganas el juego!", COLUMNA_POWERUPS, 250, 480, Align.left, true);
		font.draw(batch, "Controles: flechas = moverte  |  ESPACIO = disparar  |  P = pausa",
				COLUMNA_ENEMIGOS, 125, Constantes.ANCHO - 100, Align.left, false);

		botones.dibujar(batch, font);
	}

	// Un subtítulo de sección
	private void titulo(SpriteBatch batch, BitmapFont font, String texto, float x, float y)
	{
		font.getData().setScale(1.5f);
		font.setColor(0.4f, 0.9f, 1f, 1f);
		font.draw(batch, texto, x, y);
		font.setColor(Color.WHITE);
	}

	// Una fila: la imagen a la izquierda, y a la derecha el nombre y la descripción
	private void fila(SpriteBatch batch, BitmapFont font, Texture imagen, String nombre, String descripcion,
			float x, float y, float lado, float anchoTexto)
	{
		// la imagen se agranda sin deformarse hasta caber en un cuadrado de "lado" píxeles
		float escala = lado / Math.max(imagen.getWidth(), imagen.getHeight());
		float ancho = imagen.getWidth() * escala;
		float alto = imagen.getHeight() * escala;
		batch.draw(imagen, x + (80 - ancho) / 2, y - 70 + (80 - alto) / 2 + (80 - lado) / 2, ancho, alto);

		font.getData().setScale(1.5f);
		font.draw(batch, nombre, x + 100, y);
		font.getData().setScale(1f);
		font.draw(batch, descripcion, x + 100, y - 35, anchoTexto, Align.left, true);
		font.getData().setScale(1.5f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (eleccion == 0)
		{
			game.setScreen(new PantallaSeleccion(game));
		}
		else if (eleccion == 1 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
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
