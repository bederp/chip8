package com.pbeder.chip8;

//The original implementation of the Chip-8 language used a 64x32-pixel monochrome display with this format:
//        (0,0)     (63,0)
//        (0,31)	(63,31)
class Chip8Screen {
    private boolean[][] screenConfiguration = new boolean[Chip8.SCREEN_HEIGHT][Chip8.SCREEN_WIDTH];
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
        y = (byte) (y % 32);
        final boolean[] line = screenConfiguration[y];
        for (int i = 0; i < 8; i++) {
            int currentX = (x + i) % 64;
            boolean currentSpriteBit = (sprite >> (7 - i) & 0x1) != 0;
            checkCollision(line[currentX], currentSpriteBit);
            line[currentX] = currentSpriteBit;
        }
    }

    private void checkCollision(boolean screenPixel, boolean spritePixel) {
        if (screenPixel && spritePixel) {
            chip8.setCarry(true);
        }
    }

    void setPixel(byte x, byte y) {
        boolean[] line = screenConfiguration[y];
        line[x] = true;
    }
}
