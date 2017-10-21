package com.pbeder.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pbeder.Application;
import org.lwjgl.Sys;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Chip8";
        config.width = Application.WORLD_WIDTH * 20;
        config.height = Application.WORLD_HEIGHT * 20;
        if (arg.length != 1) {
            System.out.println("Please provide file to load");
            System.exit(-1);
        }
        new LwjglApplication(new Application(arg[0]), config);
	}
}
