package com.pbeder.chip8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

import static com.pbeder.chip8.Fonts.*;
import static java.lang.System.arraycopy;

public class Chip8 {

    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;
    private static final int MEMORY_SIZE = 4096;
    private static final int NUMBER_OF_REGISTERS = 16;
    private static final int RECURSION_DEPTH = 16;
    private static final int DEFAULT_PROGRAM_LOCATION = 0x200;
    byte[] memory = new byte[MEMORY_SIZE];
    byte[] registers = new byte[NUMBER_OF_REGISTERS]; // Also called V(0-F)
    short I;
    short pc;
    byte stackPointer;
    short[] stack = new short[RECURSION_DEPTH];
    short delayTimer = -1;
    short soundTimer = -1;
    private Cpu cpu = new Cpu(this);
    private Supplier<Byte> randomGenerator;
    private Chip8Screen screen;
    private Chip8Keyboard keyboard;
    private Chip8Beeper beeper;

    public Chip8(Chip8Beeper beeper) {
        arraycopy(FONTS, 0, memory, 0, NUMBER_OF_FONTS * FONT_HEIGHT);
        randomGenerator = new RandomByteSupplier();
        screen = new Chip8Screen(this);
        keyboard = new Chip8Keyboard();
        this.beeper = beeper;
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

    public boolean[][] getScreen() {
        return screen.getScreen();
    }

    void drawByte(byte x, byte y, byte sprite) {
        screen.writeSprite(x, y, sprite);
    }

    void setPixel(byte x, byte y) {
        screen.setPixel(x, y);
    }

    public void loadFromFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            arraycopy(bytes, 0, memory, DEFAULT_PROGRAM_LOCATION, bytes.length);
            pc = DEFAULT_PROGRAM_LOCATION;
        } catch (IOException e) {
            System.out.println("Something wrong with provided file");
            e.printStackTrace();
        }
    }

    public void stepTimes(int x) {
        for (int i = 0; i < x; i++) {
            handleOpcode(getOpcode());
        }
        stepDelayTimer();
        stepSoundTimer();
    }

    private void stepSoundTimer() {
        if (soundTimer > 0) {
            soundTimer--;
            beeper.beep();
        }
    }

    private void stepDelayTimer() {
        if (delayTimer > 0) {
            delayTimer--;
        }
    }

    void clearScreen() {
        screen.clear();
    }

    boolean isKeyPressed(byte x) {
        return keyboard.isKeyPressed(x);
    }

    byte getFirstKeyPressed() {
        return keyboard.getFirstKeyPressed();
    }

    boolean isAnyKeyPressed() {
        return keyboard.isAnyKeyPressed();
    }

    public void setKey(byte key, boolean isPressed) {
        keyboard.setKey(key, isPressed);
    }

    private short getOpcode() {
        return (short) (memory[pc] << 8 | memory[pc + 1] & 0xFF);
    }
}
