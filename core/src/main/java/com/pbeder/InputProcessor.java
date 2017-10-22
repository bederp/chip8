package com.pbeder;

import com.badlogic.gdx.InputAdapter;
import com.pbeder.chip8.Chip8;

import static com.badlogic.gdx.Input.Keys.*;

public class InputProcessor extends InputAdapter {

    private static final int INVALID_INPUT = 0x10;
    private Chip8 chip8;

    InputProcessor(Chip8 chip8) {
        this.chip8 = chip8;
    }

    @Override
    public boolean keyDown(int keyCode) {
        byte b = toKey(keyCode);
        if (b != INVALID_INPUT) {
            chip8.setKey(b, true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        byte b = toKey(keyCode);
        if (b != INVALID_INPUT) {
            chip8.setKey(b, false);
        }
        return true;
    }

    private byte toKey(int keyCode) {
        switch (keyCode) {
            case NUM_1:
                return 0x1;
            case NUM_2:
                return 0x2;
            case NUM_3:
                return 0x3;
            case NUM_4:
                return 0xC;
            case Q:
                return 0x4;
            case W:
                return 0x5;
            case E:
                return 0x6;
            case R:
                return 0xD;
            case A:
                return 0x7;
            case S:
                return 0x8;
            case D:
                return 0x9;
            case F:
                return 0xE;
            case Z:
                return 0xA;
            case X:
                return 0x0;
            case C:
                return 0xB;
            case V:
                return 0xF;
            default:
                System.out.println("INVALID KEY PRESSED");
                return (byte) INVALID_INPUT;
        }
    }
}
