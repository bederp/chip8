package com.pbeder.chip8;

import static com.pbeder.chip8.Chip8.SCREEN_HEIGHT;
import static com.pbeder.chip8.Chip8.SCREEN_WIDTH;
import static java.lang.Byte.toUnsignedInt;

//The original implementation of the Chip-8 language used a 64x32-pixel monochrome display with this format:
//        (0,0)     (63,0)
//        (0,31)	(63,31)
class Screen {
    private boolean[][] screen = new boolean[SCREEN_HEIGHT][SCREEN_WIDTH];
    private Chip8 chip8;
    private boolean wrapScreen = true;

    Screen(Chip8 chip8) {
        this.chip8 = chip8;
    }

    Screen(Chip8 chip8, boolean wrapScreen) {
        this(chip8);
        this.wrapScreen = wrapScreen;
    }

    boolean[][] getScreen() {
        return screen;
    }

    void drawSprite(byte x, byte y, byte[] sprite) {
        for (int i = 0; i < sprite.length; i++) {
            byte yy = getY(y, i);
            byte spriteLine = sprite[i];
            drawLine(x, yy, spriteLine);
        }
    }

    private byte getY(byte y, int i) {
        if (wrapScreen) {
            return (byte) ((toUnsignedInt(y) + i) % 32);
        } else {
            return y;
        }
    }

    private void drawLine(byte x, byte y, byte sprite) {
        final boolean[] screenLine = screen[y];
        for (int i = 0; i < 8; i++) {
            int currentX = getX(x, i);
            boolean currentSpriteBit = (sprite >> (7 - i) & 0x1) != 0;
            checkCollision(screenLine[currentX], currentSpriteBit);
            screenLine[currentX] = screenLine[currentX] ^ currentSpriteBit;
        }
    }

    private byte getX(byte x, int i) {
        if (wrapScreen) {
            return (byte) ((toUnsignedInt(x) + i) % 64);
        } else {
            return x;
        }
    }

    private void checkCollision(boolean screenPixel, boolean spritePixel) {
        if (screenPixel && spritePixel) {
            chip8.setCarry(true);
        }
    }

    void setPixel(byte x, byte y) {
        screen[y][x] = true;
    }

    void clear() {
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                screen[i][j] = false;
            }
        }
    }
}
