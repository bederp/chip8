package com.pbeder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pbeder.chip8.Chip8;

import java.io.File;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

public class Application extends ApplicationAdapter {
    public static final int WORLD_WIDTH = Chip8.SCREEN_WIDTH;
    public static final int WORLD_HEIGHT = Chip8.SCREEN_HEIGHT;
    private static final String LOG = Application.class.getSimpleName();
    private Chip8 chip8;
    private InputProcessor keyboard;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private FPSLogger fps;
    private final File file;

    public Application(String path) {
        file = new File(path);
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
    }


    @Override
    public void create() {
        chip8();
        app.log(LOG, "Creating game");
        fps = new FPSLogger();
        Gdx.input.setInputProcessor(keyboard);
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true);
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void chip8() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("core/out/production/resources/sounds/beep.wav"));
        chip8 = new Chip8(sound::play);
        chip8.loadFromFile(file);
        keyboard = new InputProcessor(chip8);
    }

    @Override
    public void render() {
        chip8.fpsStep();
        draw(chip8.getScreen());
        fps.log();
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

    private void draw(boolean[][] screen) {
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
}
