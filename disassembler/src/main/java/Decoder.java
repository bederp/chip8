public class Decoder {

    String decode(short opcode, int pc) {
        switch (opcode & 0xF000) {
            case 0x0000:
                _0(opcode);
                break;
            case 0x1000:
                return "JP " + getNNN(opcode);
            case 0x2000:
                return "CALL " + getNNN(opcode);
            case 0x3000:
                return "SE Vx, " + getKK(opcode);
            case 0x4000:
                return "SNE Vx, " + getKK(opcode);
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
                return "DRW V" + getX(opcode) + ", V" + getY(opcode) + ", " + getKK(opcode);
            case 0xE000:
                _E(opcode);
                break;
            case 0xF000:
                _F(opcode);
                break;
        }
    }

     String _F(short opcode) {
        switch (opcode & 0x00FF) {
            case 0x0007:
                return "LD V" + getX(opcode) + ", DT";
            case 0x000A:
                return "LD V" + getX(opcode) + ", K";
            case 0x0015:
                return "LD DT, V" + getX(opcode);
                break;
            case 0x0018:
                return "LD ST, V" + getX(opcode);
            case 0x001E:
                _0xFx1E(opcode);
                break;
            case 0x0029:
                _0xFx29(opcode);
                break;
            case 0x0033:
                _0xFx33(opcode);
                break;
            case 0x0055:
                _0xFx55(opcode);
                break;
            case 0x0065:
                _0xFx65(opcode);
                break;
        }
    }

    String _E(short opcode) {
        switch (opcode & 0x00FF) {
            case 0x009E:
                return "SKP V" + getX(opcode);
            case 0x00A1:
                return "SKNP V" + getX(opcode);
            default:
                return "UNK";
        }
    }

    String _8(short opcode) {
        switch (opcode & 0x000F) {
            case 0x0000:
                return "LD V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0001:
                return "OR V" + getX(opcode) + ", V" + getY(opcode);
            case 0x0002:
                return "AND V" + getX(opcode) + ", V" + getY(opcode);
            break;
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
        }
    }

    String _0(short opcode) {
        switch (opcode) {
            case 0x00E0:
                return "CLS";
            case 0x00EE:
                return "RET";
            default:
                return "SYS" + getNNN(opcode);
        }
    }

    private byte getX(short opcode) {
        return (byte) (opcode >>> 8 & 0xF);
    }

    private byte getY(short opcode) {
        return (byte) (opcode >>> 4 & 0xF);
    }

    private byte getKK(short opcode) {
        return (byte) (opcode & 0xFF);
    }

    private byte getN(short opcode) {
        return (byte) (opcode & 0xF);
    }

    private short getNNN(short opcode) {
        return (short) (opcode & 0xFFF);
    }
}
