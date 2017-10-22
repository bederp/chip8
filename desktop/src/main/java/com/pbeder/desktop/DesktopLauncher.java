package com.pbeder.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pbeder.Application;

import java.awt.*;

public class DesktopLauncher {
    public static void main(String[] arg) {
        String file = selectRom();
        configureLibGdx(file);
    }

    private static void configureLibGdx(String file) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Chip8";
        config.width = Application.WORLD_WIDTH * 20;
        config.height = Application.WORLD_HEIGHT * 20;
        new LwjglApplication(new Application(file), config);
    }

    private static String selectRom() {
        FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory() + dialog.getFile();
        System.out.println(file + " chosen.");
        dialog.dispose();
        return file;
    }
}
