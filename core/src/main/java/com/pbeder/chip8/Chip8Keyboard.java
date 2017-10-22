package com.pbeder.chip8;

/*
 | 1 | 2 | 3 | C |
 | 4 | 5 | 6 | D |
 | 7 | 8 | 9 | E |
 | A | 0 | B | F |
*/
class Chip8Keyboard {
    private boolean[] keyboard = new boolean[16];

    boolean isKeyPressed(byte x) {
//        System.out.println(Arrays.toString(keyboard));
        return keyboard[x];
    }

    void setKey(byte key, boolean isPressed) {
        keyboard[key] = isPressed;
    }
}
