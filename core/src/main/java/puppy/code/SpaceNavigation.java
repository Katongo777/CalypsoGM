package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.util.GestorRecursos;
import puppy.code.vista.FondoEstrellado;
import puppy.code.vista.PantallaMenu;




public class SpaceNavigation extends Game {
	private String nombreJuego = "Calypso: Skill Issue Edition";
	private boolean modoDios = false; // se activa con el código Konami en el menú
	private boolean guiaVista = false; // la guía "Cómo sobrevivir" se muestra sola una vez por sesión
	private SpriteBatch batch;
	private BitmapFont font;
	private FondoEstrellado fondo;

	public void create() {
		batch = new SpriteBatch();
		font = GestorRecursos.getInstance().getFuente(); // fuente pixel-art propia
		font.getData().setScale(1.5f);
		fondo = new FondoEstrellado(); // un solo fondo para todas las pantallas
		Screen ss = new PantallaMenu(this);
		this.setScreen(ss);
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		// Los recursos compartidos se liberan una sola vez, al cerrar el juego
		GestorRecursos.getInstance().liberar();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public BitmapFont getFont() {
		return font;
	}

	public FondoEstrellado getFondo() {
		return fondo;
	}

	public boolean isGuiaVista() {
		return guiaVista;
	}

	public void setGuiaVista(boolean guiaVista) {
		this.guiaVista = guiaVista;
	}

	public boolean isModoDios() {
		return modoDios;
	}

	public void setModoDios(boolean modoDios) {
		this.modoDios = modoDios;
	}

	// El récord ahora lo guarda el Singleton GestorRecursos (y queda en un archivo)
	public int getHighScore() {
		return GestorRecursos.getInstance().getRecord();
	}
	
	

}