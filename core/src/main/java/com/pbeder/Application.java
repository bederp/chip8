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

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.pbeder.chip8.Chip8.SCREEN_HEIGHT;
import static com.pbeder.chip8.Chip8.SCREEN_WIDTH;

public class Application extends ApplicationAdapter {
    public static final int WORLD_WIDTH = SCREEN_WIDTH;
    public static final int WORLD_HEIGHT = SCREEN_HEIGHT;
    private static final String LOG = Application.class.getSimpleName();
    private final File file;
    private Chip8 chip8;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private FPSLogger fps;

    public Application(String path) {
        file = new File(path);
    }


    @Override
    public void create() {
        app.log(LOG, "Creating game");
        fps = new FPSLogger();
        chip8();
        camera();
    }

    private void chip8() {
//        Sound sound = Gdx.audio.newSound(Gdx.files.internal("core/out/production/resources/sounds/beep.wav"));
        Sound sound = audio.newSound(Gdx.files.internal("sounds/beep.wav"));
        chip8 = new Chip8(sound::play);
        chip8.loadFromFile(file);
        InputProcessor keyboard = new InputProcessor(chip8);
        Gdx.input.setInputProcessor(keyboard);
    }

    private void camera() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true);
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render() {
        chip8.stepTimes(4);
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
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
