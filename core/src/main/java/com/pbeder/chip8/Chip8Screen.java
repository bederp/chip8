package com.pbeder.chip8;

import static com.pbeder.chip8.Chip8.SCREEN_HEIGHT;
import static com.pbeder.chip8.Chip8.SCREEN_WIDTH;
import static java.lang.Byte.toUnsignedInt;

//The original implementation of the Chip-8 language used a 64x32-pixel monochrome display with this format:
//        (0,0)     (63,0)
//        (0,31)	(63,31)
class Chip8Screen {
    private boolean[][] screenConfiguration = new boolean[SCREEN_HEIGHT][SCREEN_WIDTH];
    private Chip8 chip8;

    Chip8Screen(Chip8 chip8) {
        this.chip8 = chip8;
    }

    boolean[][] getScreenConfiguration() {
        return screenConfiguration;
    }

    void printScreen() {
        for (boolean[] aScreenConfiguration : screenConfiguration) {
            for (boolean pixel : aScreenConfiguration) {
                System.out.print(pixel ? "*" : ".");
            }
            System.out.println();
        }
    }

    void writeSprite(byte x, byte y, byte sprite) {
        final boolean[] line = screenConfiguration[y];
        for (int i = 0; i < 8; i++) {
            int currentX = (toUnsignedInt(x) + i) % 64 ;
            boolean currentSpriteBit = (sprite >> (7 - i) & 0x1) != 0;
            checkCollision(line[currentX], currentSpriteBit);
            line[currentX] = line[currentX] ^ currentSpriteBit;
        }
    }

    private void checkCollision(boolean screenPixel, boolean spritePixel) {
        if (screenPixel && spritePixel) {
            chip8.setCarry(true);
        }
    }

    void setPixel(byte x, byte y) {
        screenConfiguration[y][x] = true;
    }

    void clear() {
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                screenConfiguration[i][j] = false;
            }
        }
    }
}
