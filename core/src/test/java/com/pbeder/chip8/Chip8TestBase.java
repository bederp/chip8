package com.pbeder.chip8;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static com.pbeder.chip8.Fonts.FONT_HEIGHT;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class Chip8TestBase {
    Chip8 chip8;

    @Before
    public void setUp() {
        chip8 = new Chip8();
    }

    void assertProgramCounterIs(int previousFrameAddress) {
        //Chip8 PC is 12 bit but in Java I've used short to represent it that's why 0xFFF
        assertThat(chip8.pc, is((short) (previousFrameAddress & 0xFFF)));
    }

    void assertStackPointerIs(int expectedStackPointer) {
        assertThat(chip8.stackPointer, is((byte) expectedStackPointer));
    }

    void assertTopStackIs(int previousPC) {
        assertThat(chip8.stack[chip8.stackPointer - 1], is(((short) previousPC)));
    }

    void assertThatRegisterXIs(byte x, int value) {
        assertThat(chip8.registers[x], is(((byte) value)));
    }

    void assertCarryIsSet() {
        assertThat(chip8.registers[0xF], is((byte) 1));
    }

    void assertCarryIsNotSet() {
        assertThat(chip8.registers[0xF], is((byte) 0));
    }

    void assertIIs(int i) {
        assertThat(chip8.I, is((short) i));
    }

    void assertMemoryIs(short address, byte value) {
        assertThat(chip8.memory[address], is(value));
    }

    // This is manual test check if console output contains default fonts -> 0-9, A-F
    @Ignore
    @Test
    public void assert_fonts_are_placed_correctly_in_memory() {
        int currentHeight = 1;
        int counter = 0;
        for (byte b : chip8.memory) {
            System.out.println(convertByteToStarsAndSpaces(b));
            if (currentHeight++ % FONT_HEIGHT == 0) {
                System.out.println();
            }
            if (++counter >= Fonts.FONTS.length) {
                return;
            }
        }
    }

    private String convertByteToStarsAndSpaces(byte b) {
        //Only high nibble (4 bytes) are used to represent font
        return Integer.toBinaryString((b >> 4) & 0xF | 0x100)
                .substring(5).replace('1', '*').replace('0', ' ');
    }

    void assertScreenConfigurationIs(boolean[][] screenConfiguration) {
        assertTrue(Arrays.deepEquals(chip8.getScreen(), screenConfiguration));
    }
}
