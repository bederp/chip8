package com.pbeder;

class AsciiDecoder {

    private static final String UPPER_BLOCK = "\u2580";
    private static final String LOWER_BLOCK = "\u2584";
    private static final String FULL_BLOCK = "\u2588";
    private static final String EMPTY_BLOCK = " ";

    String decode(short opcode) {
        byte upper = (byte) (opcode >>> 8);
        byte lower = (byte) opcode;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            String sprite = decodeBlock(isBitSet(upper, i), isBitSet(lower, i));
            sb.append(sprite);
        }
        return sb.toString();
    }

    private String decodeBlock(boolean upper, boolean lower) {
        if (upper && lower) {
            return FULL_BLOCK;
        } else if (upper) {
            return UPPER_BLOCK;
        } else if (lower) {
            return LOWER_BLOCK;
        } else {
            return EMPTY_BLOCK;
        }
    }

    public static void main(String[] args) {
        System.out.println(UPPER_BLOCK + LOWER_BLOCK + FULL_BLOCK);
    }

    private boolean isBitSet(byte b, int position) // 0-7
    {
        return (b & (1 << 7 - position)) != 0;
    }
}
