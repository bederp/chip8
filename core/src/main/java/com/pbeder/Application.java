package com.pbeder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pbeder.chip8.Chip8;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

public class Application extends ApplicationAdapter {
    public static final int WORLD_WIDTH = Chip8.SCREEN_WIDTH;
    public static final int WORLD_HEIGHT = Chip8.SCREEN_HEIGHT;
    private static final String LOG = Application.class.getSimpleName();
    private FPSLogger fps;
    private Chip8 chip8;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;


    @Override
    public void create() {
        app.log(LOG, "Creating game");
        fps = new FPSLogger();
        chip8 = new Chip8();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render() {
        chip8Screen(chip8.getScreen());
        checkerBoard();
        fps.log();
    }

    private void chip8Screen(boolean[][] screen) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(Filled);
        for (int y = 0; y < WORLD_HEIGHT; y++) {
            for (int x = 0; x < WORLD_WIDTH; x++) {
                if (screen[y][x]) {
                    shapeRenderer.rect(x, y, 1, 1);
                }
            }
        }
        shapeRenderer.end();
    }

    private void checkerBoard() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(Filled);
        for (int i = 0; i < WORLD_WIDTH; i++) {
            for (int j = 0; j < WORLD_HEIGHT; j++) {
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        shapeRenderer.rect(i + 1, j, 1, 1);
                    } else {
                        shapeRenderer.rect(i, j, 1, 1);
                    }
                }
            }
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        super.render();
    }

    @Override
    public void dispose() {
        app.log(LOG, "Disposing game");
    }
}
