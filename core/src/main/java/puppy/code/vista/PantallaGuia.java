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
import puppy.code.modelo.Personaje;
import puppy.code.modelo.niveles.DirectorNiveles;
import puppy.code.util.GestorRecursos;

/*
    Pantalla "Cómo sobrevivir": explica los enemigos, la campaña, los power-ups, las habilidades
    especiales y los controles, en dos páginas. Se muestra automáticamente la primera vez que el
    jugador entra a jugar, y después se puede abrir con la tecla H desde el menú.

    Hereda de PantallaBase (Template Method): solo define qué dibuja y a dónde va.
*/
public class PantallaGuia extends PantallaBase
{
	private static final float COLUMNA_IZQUIERDA = 50;
	private static final float COLUMNA_DERECHA = 680;

	private int pagina = 1;
	private final MenuBotones botones = new MenuBotones(new String[] { "PÁGINA 2 >", "CONTINUAR", "VOLVER AL MENÚ" },
			Constantes.ANCHO / 2f - 480, 22, 300, 54, 30, true);
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
		if (eleccion == 0)
		{
			pagina = pagina == 1 ? 2 : 1;
			botones.setEtiqueta(0, pagina == 1 ? "PÁGINA 2 >" : "< PÁGINA 1");
		}
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		font.getData().setScale(3f);
		font.setColor(1f, 0.85f, 0.2f, 1f);
		font.draw(batch, "CÓMO SOBREVIVIR (" + pagina + "/2)", 0, Constantes.ALTO - 15, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		if (pagina == 1)
		{
			dibujarPaginaEnemigos(batch, font);
		}
		else
		{
			dibujarPaginaNave(batch, font);
		}
		botones.dibujar(batch, font);
	}

	private void dibujarPaginaEnemigos(SpriteBatch batch, BitmapFont font)
	{
		GestorRecursos r = GestorRecursos.getInstance();
		titulo(batch, font, "ENEMIGOS", COLUMNA_IZQUIERDA, 700);
		fila(batch, font, r.getTextura("bot.png"), "BOT", "Dispara balas lentas hacia abajo desde la oleada 2. 1 vida.", COLUMNA_IZQUIERDA, 660, 64, 470);
		fila(batch, font, r.getTextura("lag.png"), "LAG", "Se teletransporta, pero tiembla justo antes de saltar. 1 vida.", COLUMNA_IZQUIERDA, 575, 64, 470);
		fila(batch, font, r.getTextura("cheater.png"), "CHEATER", "Aguanta 3 balas, te apunta con 3 disparos y al morir suelta 2 Lags.", COLUMNA_IZQUIERDA, 490, 64, 470);
		fila(batch, font, r.getTextura("spammer.png"), "SPAMMER", "Desde la oleada 6. Flota de lado a lado y dispara ráfagas de 3 balas.", COLUMNA_IZQUIERDA, 405, 64, 470);
		fila(batch, font, r.getTextura("sniper.png"), "SNIPER", "Desde la oleada 11. Apunta con una línea roja y dispara rápido: ¡esquiva!", COLUMNA_IZQUIERDA, 320, 64, 470);
		fila(batch, font, r.getTextura("jefe_toxic.png"), "JEFES", "Oleadas 5, 10, 15 y 20. Tienen barra de vida, ataques propios y un escudo.", COLUMNA_IZQUIERDA, 235, 64, 470);

		titulo(batch, font, "LA CAMPAÑA", COLUMNA_DERECHA, 700);
		font.getData().setScale(1f);
		font.draw(batch, "Son " + DirectorNiveles.TOTAL_OLEADAS + " oleadas en 4 bloques. Al final de cada bloque te espera un jefe:\n"
				+ "5 EL TOXIC, 10 EL HACKER, 15 EL SWEATY y 20 EL ADMIN (el jefe final).\n\n"
				+ "Cuando a un jefe le baja la vida se pone un ESCUDO: derrota a los enemigos que invoca para romperlo y sigue disparándole. "
				+ "¡Las líneas rojas avisan dónde va a caer un rayo!\n\n"
				+ "Las oleadas 4, 8, 12, 17 y 19 son HARDCORE: más enemigos y más rápidos, pero caen más power-ups.\n\n"
				+ "Los enemigos entran en grupos por arriba y se mueven en la parte de arriba de la pantalla: "
				+ "abajo tienes espacio para maniobrar.", COLUMNA_DERECHA, 650, 490, Align.left, true);
	}

	private void dibujarPaginaNave(SpriteBatch batch, BitmapFont font)
	{
		GestorRecursos r = GestorRecursos.getInstance();
		titulo(batch, font, "POWER-UPS (los sueltan los enemigos)", COLUMNA_IZQUIERDA, 700);
		fila(batch, font, r.getTextura("pw_pizza.png"), "PIZZA", "+1 vida.", COLUMNA_IZQUIERDA, 660, 56, 470);
		fila(batch, font, r.getTextura("pw_silla.png"), "SILLA GAMER", "Escudo durante 10 segundos.", COLUMNA_IZQUIERDA, 580, 56, 470);
		fila(batch, font, r.getTextura("pw_rgb.png"), "RGB", "Disparo doble por 10 segundos.", COLUMNA_IZQUIERDA, 500, 56, 470);
		fila(batch, font, r.getTextura("pw_lootbox.png"), "LOOT BOX", "Te da un arma al azar por un rato.", COLUMNA_IZQUIERDA, 420, 56, 470);

		titulo(batch, font, "HABILIDAD ESPECIAL (tecla X)", COLUMNA_DERECHA, 700);
		font.getData().setScale(1f);
		font.draw(batch, "La barra se llena eliminando enemigos y baja un poco cuando te golpean. Con la barra llena, aprieta X:",
				COLUMNA_DERECHA, 662, 490, Align.left, true);
		float y = 560;
		for (Personaje p : Personaje.values())
		{
			fila(batch, font, r.getTextura(p.getArchivoNave()), p.crearHabilidad().getNombre().toUpperCase() + " (" + p.getNombre() + ")",
					p.crearHabilidad().getDescripcion() + ".", COLUMNA_DERECHA, y, 56, 410);
			y -= 78;
		}

		titulo(batch, font, "CONTROLES", COLUMNA_IZQUIERDA, 300);
		font.getData().setScale(1f);
		font.draw(batch, "Flechas: moverte\nESPACIO (mantener): disparar\nX: habilidad especial (con la barra llena)\n"
				+ "P o ESC: pausa\nENTER: saltar el cartel de la oleada", COLUMNA_IZQUIERDA, 262, 560, Align.left, true);

		titulo(batch, font, "CONSEJOS", COLUMNA_DERECHA, 300);
		font.getData().setScale(1f);
		font.draw(batch, "Después de un golpe eres invulnerable 3 segundos, pero haces la mitad de daño.\n"
				+ "En Opciones puedes cambiar el volumen y la dificultad (Fácil te da una vida extra).",
				COLUMNA_DERECHA, 262, 490, Align.left, true);
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
		batch.draw(imagen, x + (lado - ancho) / 2, y - 62 + (lado - alto) / 2, ancho, alto);

		font.getData().setScale(1.5f);
		font.draw(batch, nombre, x + lado + 20, y);
		font.getData().setScale(1f);
		font.draw(batch, descripcion, x + lado + 20, y - 33, anchoTexto, Align.left, true);
		font.getData().setScale(1.5f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (eleccion == 1)
		{
			game.setScreen(new PantallaSeleccion(game));
		}
		else if (eleccion == 2 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
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
