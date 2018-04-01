package com.pbeder;

import static java.lang.String.format;

class Decoder {

    private static final String OPCODE_NOT_RECOGNIZED = "Unknown";

    String decode(short opcode) {
        switch (opcode & 0xF000) {
            case 0x0000:
                return _0(opcode);
            case 0x1000:
                return "JP " + getNNN(opcode);
            case 0x2000:
                return "CALL " + getNNN(opcode);
            case 0x3000:
                return "SE V" + getX(opcode) + ", " + getKK(opcode);
            case 0x4000:
                return "SNE V" + getX(opcode) + ", " + getKK(opcode);
            case 0x5000:
                return "SE V" + getX(opcode) + " V" + getY(opcode);
            case 0x6000:
                return "LD V" + getX(opcode) + ", " + getKK(opcode);
            case 0x7000:
                return "ADD V" + getX(opcode) + ", " + getKK(opcode);
            case 0x8000:
                return _8(opcode);
            case 0x9000:
                return "SNE V" + getX(opcode) + ", V" + getY(opcode);
            case 0xA000:
                return "LD I, " + getNNN(opcode);
            case 0xB000:
                return "JP V0, " + getNNN(opcode);
            case 0xC000:
                return "RND V" + getX(opcode) + ", " + getKK(opcode);
            case 0xD000:
                return "DRW V" + getX(opcode) + ", V" + getY(opcode) + ", " + getN(opcode);
            case 0xE000:
                return _E(opcode);
            case 0xF000:
                return _F(opcode);
            default:
                return OPCODE_NOT_RECOGNIZED;
        }
    }

    private String _F(short opcode) {
        switch (opcode & 0x00FF) {
            case 0x0007:
                return "LD V" + getX(opcode) + ", DT";
            case 0x000A:
                return "LD V" + getX(opcode) + ", K";
            case 0x0015:
                return "LD DT, V" + getX(opcode);
            case 0x0018:
                return "LD ST, V" + getX(opcode);
            case 0x001E:
                return "ADD I, V" + getX(opcode);
            case 0x0029:
                return "LD F, V" + getX(opcode);
            case 0x0033:
                return "LD B, V" + getX(opcode);
            case 0x0055:
                return "LD [I], V" + getX(opcode);
            case 0x0065:
                return "LD V" + getX(opcode) + ", [I]";
            default:
                return OPCODE_NOT_RECOGNIZED;
        }
    }

    private String _E(short opcode) {
        switch (opcode & 0x00FF) {
            case 0x009E:
                return "SKP V" + getX(opcode);
            case 0x00A1:
                return "SKNP V" + getX(opcode);
            default:
                return OPCODE_NOT_RECOGNIZED;
        }
    }

    private String _8(short opcode) {
        switch (opcode & 0x000F) {
            case 0x0000:
                return "LD V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0001:
                return "OR V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0002:
                return "AND V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0003:
                return "XOR V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0004:
                return "ADD V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0005:
                return "SUB V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0006:
                return "SHR V" + getX(opcode);
            case 0x0007:
                return "SUBN V" + getX(opcode) + ", V" + getY(opcode);
            case 0x000E:
                return "SHL V" + getX(opcode);
            default:
                return OPCODE_NOT_RECOGNIZED;
        }
    }

    private String _0(short opcode) {
        switch (opcode) {
            case 0x00E0:
                return "CLS";
            case 0x00EE:
                return "RET";
            default:
                return "SYS " + getNNN(opcode);
        }
    }

    private String getX(short opcode) {
        byte x = (byte) (opcode >>> 8 & 0xF);
        return format("%x", x);
    }

    private String getY(short opcode) {
        byte y = (byte) (opcode >>> 4 & 0xF);
        return format("%x", y);
    }

    private String getKK(short opcode) {
        byte kk = (byte) (opcode & 0xFF);
        return format("%02x", kk);
    }

    private String getN(short opcode) {
        byte n = (byte) (opcode & 0xF);
        return format("%x", n);
    }

    private String getNNN(short opcode) {
        short nnn = (short) (opcode & 0xFFF);
        return format("%03x", nnn);
    }
}
