package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.util.GestorRecursos;
import puppy.code.vista.FondoEstrellado;
import puppy.code.vista.PantallaMenu;




public class SpaceNavigation extends Game {
	private String nombreJuego = "Space Navigation";
	private SpriteBatch batch;
	private BitmapFont font;
	private FondoEstrellado fondo;

	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(); // usa Arial font x defecto
		font.getData().setScale(2f);
		fondo = new FondoEstrellado(); // un solo fondo para todas las pantallas
		Screen ss = new PantallaMenu(this);
		this.setScreen(ss);
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
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

	// El récord ahora lo guarda el Singleton GestorRecursos (y queda en un archivo)
	public int getHighScore() {
		return GestorRecursos.getInstance().getRecord();
	}
	
	

}