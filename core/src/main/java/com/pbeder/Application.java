package com.pbeder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pbeder.chip8.Chip8;
import com.pbeder.chip8.Chip8Keyboard;

import static com.badlogic.gdx.Gdx.app;

public class Application extends ApplicationAdapter {
    private static final String LOG = Application.class.getSimpleName();

    private SpriteBatch batch;
	private Texture img;
	private FPSLogger fps;
    public Chip8 chip8;


	@Override
	public void create () {
	    app.log(LOG, "Creating game");
	    fps = new FPSLogger();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		chip8 = new Chip8();
	}

	@Override
	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
		fps.log();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
        app.log(LOG, "Disposing game");
	}
}
