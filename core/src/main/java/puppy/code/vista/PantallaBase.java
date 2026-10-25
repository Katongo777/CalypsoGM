package puppy.code.vista;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.Constantes;
import puppy.code.SpaceNavigation;

/*
    PATRÓN TEMPLATE METHOD (GM-8)

    Problema: las pantallas del juego (menú, juego, game over...) repetían el mismo
    código en cada render(): limpiar la pantalla, configurar la cámara, abrir y
    cerrar el batch... Además cada una tenía 6 métodos vacíos de la interfaz Screen.

    Solución: esta clase abstracta define el ESQUELETO del render() una sola vez
    (el "template") y lo deja FINAL, así ninguna hija puede cambiar el orden:

        1. actualizar(delta)    -> la hija actualiza su lógica     (abstracto)
        2. dibujar()            -> la hija dibuja lo suyo          (abstracto)
        3. revisarTransicion()  -> la hija decide si cambia de pantalla (gancho opcional)

    Las pantallas hijas solo rellenan los pasos; el orden lo manda esta clase.
*/
public abstract class PantallaBase implements Screen
{
    protected final SpaceNavigation game;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;

    protected PantallaBase(SpaceNavigation game)
    {
        this.game = game;
        camera = new OrthographicCamera();
        // FitViewport mantiene la proporción del mundo cuando se cambia el tamaño de la ventana
        viewport = new FitViewport(Constantes.ANCHO, Constantes.ALTO, camera);
    }

    // El método plantilla: es final, el orden de los pasos no se puede alterar
    @Override
    public final void render(float delta)
    {
        ScreenUtils.clear(0.02f, 0.02f, 0.1f, 1);

        actualizar(delta);                                   // paso 1
        game.getFondo().actualizar();

        viewport.apply();
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getFondo().dibujar(game.getBatch());            // el fondo de estrellas va siempre atrás
        dibujar();                                           // paso 2
        game.getBatch().end();

        revisarTransicion();                                 // paso 3
    }

    // Pasos obligatorios: cada pantalla hija DEBE implementarlos
    protected abstract void actualizar(float delta);

    protected abstract void dibujar();

    // Paso opcional (gancho): por defecto no hace nada
    protected void revisarTransicion()
    {
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    // Estos métodos de Screen casi nunca se usan: se dejan vacíos aquí una sola vez
    @Override
    public void show() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
