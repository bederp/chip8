package com.pbeder.chip8;

import java.util.Random;
import java.util.function.Supplier;

class RandomByte extends Random implements Supplier<Byte> {

    private static final int BYTE_MASK = 256;

    @Override
    public Byte get() {
        return (byte) this.nextInt(BYTE_MASK);
    }
}
