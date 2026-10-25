package puppy.code.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;
import puppy.code.util.GestorRecursos;
import puppy.code.util.Opciones;

/*
    Pantalla de opciones: volumen de la música, volumen de los efectos y dificultad.
    Se puede abrir desde el menú y desde la pausa, por eso recibe la pantalla a la que
    hay que volver. Hereda de PantallaBase (Template Method).

    Con el teclado: arriba/abajo para elegir, izquierda/derecha para cambiar el valor.
    Con el mouse: un clic en un botón cambia su valor al siguiente.
*/
public class PantallaOpciones extends PantallaBase
{
	private static final int MUSICA = 0;
	private static final int EFECTOS = 1;
	private static final int DIFICULTAD = 2;
	private static final int VOLVER = 3;

	private final Screen volverA;
	private final Opciones opciones = GestorRecursos.getInstance().getOpciones();
	private final MenuBotones botones = new MenuBotones(new String[] { "", "", "", "VOLVER" },
			Constantes.ANCHO / 2f - 280, 540, 560, 60, 24, false);
	private boolean volver = false;

	public PantallaOpciones(SpaceNavigation game, Screen volverA)
	{
		super(game);
		this.volverA = volverA;
		actualizarEtiquetas();
	}

	@Override
	protected void actualizar(float delta)
	{
		int clic = botones.actualizar(viewport);
		if (clic == VOLVER)
		{
			volver = true;
		}
		else if (clic >= 0)
		{
			cambiar(clic, 1); // un clic cambia el valor al siguiente
		}
		else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
		{
			cambiar(botones.getSeleccion(), -1);
		}
		else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
		{
			cambiar(botones.getSeleccion(), 1);
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
		{
			volver = true;
		}
		actualizarEtiquetas();
	}

	// Cambia el valor de la opción elegida (paso = 1 o -1)
	private void cambiar(int opcion, int paso)
	{
		GestorRecursos recursos = GestorRecursos.getInstance();
		if (opcion == MUSICA)
		{
			opciones.setVolumenMusica(siguienteVolumen(opciones.getVolumenMusica(), paso));
			recursos.aplicarVolumenMusica();
		}
		else if (opcion == EFECTOS)
		{
			opciones.setVolumenEfectos(siguienteVolumen(opciones.getVolumenEfectos(), paso));
			recursos.reproducirSonido("explosion.ogg"); // para escuchar cómo suena
		}
		else if (opcion == DIFICULTAD)
		{
			opciones.setDificultad(opciones.getDificultad().siguiente(paso));
		}
	}

	// El volumen sube o baja de a 10%; al pasarse da la vuelta
	private float siguienteVolumen(float actual, int paso)
	{
		int porcentaje = Math.round(actual * 10) + paso;
		if (porcentaje > 10)
		{
			porcentaje = 0;
		}
		if (porcentaje < 0)
		{
			porcentaje = 10;
		}
		return porcentaje / 10f;
	}

	private void actualizarEtiquetas()
	{
		botones.setEtiqueta(MUSICA, "Música: < " + Math.round(opciones.getVolumenMusica() * 100) + "% >");
		botones.setEtiqueta(EFECTOS, "Efectos: < " + Math.round(opciones.getVolumenEfectos() * 100) + "% >");
		botones.setEtiqueta(DIFICULTAD, "Dificultad: < " + opciones.getDificultad().getNombre() + " >");
	}

	@Override
	protected void dibujar()
	{
		SpriteBatch batch = game.getBatch();
		BitmapFont font = game.getFont();

		font.getData().setScale(3f);
		font.setColor(1f, 0.85f, 0.2f, 1f);
		font.draw(batch, "OPCIONES", 0, Constantes.ALTO - 60, Constantes.ANCHO, Align.center, false);
		font.setColor(Color.WHITE);

		botones.dibujar(batch, font);

		font.getData().setScale(1f);
		font.draw(batch, "Flechas arriba/abajo: elegir   |   izquierda/derecha o clic: cambiar   |   ESC: volver",
				0, 120, Constantes.ANCHO, Align.center, false);
		font.draw(batch, "La dificultad cambia la velocidad de los enemigos, cuánto disparan y tus vidas.",
				0, 85, Constantes.ANCHO, Align.center, false);
		font.getData().setScale(1.5f);
	}

	@Override
	protected void revisarTransicion()
	{
		if (volver)
		{
			opciones.guardar();
			game.setScreen(volverA);
		}
	}
}
