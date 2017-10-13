package com.pbeder.chip8;

import java.util.function.Supplier;

import static  com.pbeder.chip8.Fonts.*;
import static java.lang.System.arraycopy;

class Chip8 {

    private static final int MEMORY_SIZE = 4096;
    private static final int NUMBER_OF_REGISTERS = 16;
    private static final int RECURSION_DEPTH = 16;

    private Cpu cpu = new Cpu(this);
    private Supplier<Byte> randomGenerator;
    private Chip8Screen screen;

    byte[] memory = new byte[MEMORY_SIZE];
    byte[] registers = new byte[NUMBER_OF_REGISTERS]; // Also called V(0-F)
    short I;
    short programCounter;
    byte stackPointer;
    short[] stack = new short[RECURSION_DEPTH];
    boolean clearScreen;
    private byte delayTimer;
    private byte soundTimer;

    Chip8() {
        arraycopy(FONTS, 0, memory, 0, NUMBER_OF_FONTS * FONT_HEIGHT);
        randomGenerator = new RandomByteSupplier();
        screen = new Chip8Screen(this);
    }

    void setRandomGenerator(Supplier<Byte> randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    void handleOpcode(short opcode) {
        cpu.handle(opcode);
    }

    void setCarry(boolean b) {
        registers[0xF] = (byte) (b ? 1 : 0);
    }

    byte getRandomByte() {
        return randomGenerator.get();
    }

    boolean[][] getScreenConfiguration() {
        return screen.getScreenConfiguration();
    }

    void writeSprite(byte x, byte y, byte sprite) {
        screen.writeSprite(x, y, sprite);
    }

    void setPixel(byte x, byte y) {
        screen.setPixel(x, y);
    }
}
