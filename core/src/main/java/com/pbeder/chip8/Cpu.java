package com.pbeder.chip8;

class Cpu {

    private static final int INSTRUCTION_SIZE_IN_BYTES = 2;
    private final Chip8 chip8;

    Cpu(Chip8 chip8) {
        this.chip8 = chip8;
    }

    void handle(short opcode) {
        chip8.pc+= INSTRUCTION_SIZE_IN_BYTES;
        switch (opcode & 0xF000) {
            case 0x0000:
                _0(opcode);
                break;
            case 0x1000:
                _0x1nnn(opcode);
                break;
            case 0x2000:
                _0x2nnn(opcode);
                break;
            case 0x3000:
                _0x3xkk(opcode);
                break;
            case 0x4000:
                _0x4xkk(opcode);
                break;
            case 0x5000:
                _0x5xy0(opcode);
                break;
            case 0x6000:
                _0x6xkk(opcode);
                break;
            case 0x7000:
                _0x7xkk(opcode);
                break;
            case 0x8000:
                _8(opcode);
                break;
            case 0x9000:
                _0x9xy0(opcode);
                break;
            case 0xA000:
                _0xAnnn(opcode);
                break;
            case 0xB000:
                _0xBnnn(opcode);
                break;
            case 0xC000:
                _0xCxkk(opcode);
                break;
            case 0xD000:
                _0xDxyn(opcode);
                break;
        }
    }

    private void _8(short opcode) {
        switch (opcode & 0x000F) {
            case 0x0000:
                _0x8xy0(opcode);
                break;
            case 0x0001:
                _0x8xy1(opcode);
                break;
            case 0x0002:
                _0x8xy2(opcode);
                break;
            case 0x0003:
                _0x8xy3(opcode);
                break;
            case 0x0004:
                _0x8xy4(opcode);
                break;
            case 0x0005:
                _0x8xy5(opcode);
                break;
            case 0x0006:
                _0x8xy6(opcode);
                break;
            case 0x0007:
                _0x8xy7(opcode);
                break;
            case 0x000E:
                _0x8xyE(opcode);
                break;
        }
    }

    private void _0(short opcode) {
        switch (opcode) {
            case 0x00E0:
                _0x00E0();
                break;
            case 0x00EE:
                _0x00EE();
                break;
            default:
                _0nnn();
        }
    }

    /*
        Dxyn - DRW Vx, Vy, nibble
        Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
        The interpreter reads n bytes from memory, starting at the address stored in I. These bytes are then displayed as sprites on screen at coordinates (Vx, Vy). Sprites are XORed onto the existing screen. If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0. If the sprite is positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the screen. See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the Chip-8 screen and sprites.
    */
    private void _0xDxyn(short opcode) {
        byte x = getX(opcode);
        byte y = getY(opcode);
        byte xx = chip8.registers[x];
        byte yy = chip8.registers[y];
        final byte n = (byte) (opcode & 0xF);
        for (int i = 0; i < n; i++) {
            final byte sprite = chip8.memory[chip8.I + i];
            chip8.writeSprite(xx, (byte) ((yy + i) % 32), sprite);
        }
    }

    /*
        Cxkk - RND Vx, byte
        Set Vx = random byte AND kk.
        The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk. The results are stored in Vx. See instruction 8xy2 for more information on AND.
    */
    private void _0xCxkk(short opcode) {
        //TODO continue opcode refactor
        byte x;
        byte kk;
        x = (byte) (opcode >> 8 & 0xF);
        kk = (byte) (opcode & 0x00FF);
        final byte randomByte = chip8.getRandomByte();
        chip8.registers[x] = (byte) (randomByte & kk);
    }

    /*
        Bnnn - JP V0, addr
        Jump to location nnn + V0.
        The program counter is set to nnn plus the value of V0.
    */
    private void _0xBnnn(short opcode) {
        short nnn = (short) (opcode & 0x0FFF);
        chip8.pc = (short) (nnn + chip8.registers[0]);
    }

    /*
        Annn - LD I, addr
        Set I = nnn.
        The value of register I is set to nnn.
    */
    private void _0xAnnn(short opcode) {
        chip8.I = (short) (opcode & 0x0FFF);
    }


    /*
        9xy0 - SNE Vx, Vy
        Skip next instruction if Vx != Vy.
        The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
    */
    private void _0x9xy0(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        if (chip8.registers[x] != chip8.registers[y]) {
            chip8.pc+= INSTRUCTION_SIZE_IN_BYTES;
        }
    }

    /*
        8xyE - SHL Vx {, Vy}
        Set Vx = Vx SHL 1.
        If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
    */
    private void _0x8xyE(short opcode) {
        byte x;
        x = ((byte) (opcode >>> 8 & 0xF));
        final int msb = 0x80 & chip8.registers[x];
        chip8.setCarry(msb >>> 7 == 1);
        chip8.registers[x] = (byte) (chip8.registers[x] << 1);
    }

    /*
        8xy7 - SUBN Vx, Vy
        Set Vx = Vy - Vx, set VF = NOT borrow.
        If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
    */
    private void _0x8xy7(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        int sum = chip8.registers[y] - chip8.registers[x];
        chip8.setCarry(Byte.toUnsignedInt(chip8.registers[y]) > Byte.toUnsignedInt(chip8.registers[x]));
        chip8.registers[x] = (byte) sum;
    }

    /*
        8xy6 - SHR Vx {, Vy}
        Set Vx = Vx SHR 1.
        If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
    */
    private void _0x8xy6(short opcode) {
        byte x;
        x = ((byte) (opcode >>> 8 & 0xF));
        final int lsb = 0x01 & chip8.registers[x];
        chip8.setCarry(lsb == 1);
        chip8.registers[x] = (byte) (chip8.registers[x] >>> 1);
    }

    /*
        8xy5 - SUB Vx, Vy
        Set Vx = Vx - Vy, set VF = NOT borrow.
        If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
    */
    private void _0x8xy5(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        int sum = chip8.registers[x] - chip8.registers[y];
        chip8.setCarry(Byte.toUnsignedInt(chip8.registers[x]) > Byte.toUnsignedInt(chip8.registers[y]));
        chip8.registers[x] = (byte) sum;
    }

    /*
        8xy4 - ADD Vx, Vy
        Set Vx = Vx + Vy, set VF = carry.
        The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
    */
    private void _0x8xy4(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        int sum = chip8.registers[x] + chip8.registers[y];
        chip8.setCarry(sum > 255);
        chip8.registers[x] = (byte) sum;
    }

    /*
        8xy3 - XOR Vx, Vy
        Set Vx = Vx XOR Vy.
        Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR compares the corresponding bits from two values, and if the bits are not both the same, then the corresponding bit in the result is set to 1. Otherwise, it is 0.
    */
    private void _0x8xy3(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        chip8.registers[x] = (byte) (chip8.registers[x] ^ chip8.registers[y]);
    }

    /*
        8xy2 - AND Vx, Vy
        Set Vx = Vx AND Vy.
        Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corresponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
    */
    private void _0x8xy2(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        chip8.registers[x] = (byte) (chip8.registers[x] & chip8.registers[y]);
    }

    /*
        8xy1 - OR Vx, Vy
        Set Vx = Vx OR Vy.
        Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the corresponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
    */
    private void _0x8xy1(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        chip8.registers[x] = (byte) (chip8.registers[x] | chip8.registers[y]);
    }

    /*
        8xy0 - LD Vx, Vy
        Set Vx = Vy.
        Stores the value of register Vy in register Vx.
    */
    private void _0x8xy0(short opcode) {
        byte x;
        byte y;
        x = ((byte) (opcode >>> 8 & 0xF));
        y = ((byte) (opcode >>> 4 & 0xF));
        chip8.registers[x] = chip8.registers[y];
    }

    /*
        7xkk - ADD Vx, byte
        Set Vx = Vx + kk.
        Adds the value kk to the value of register Vx, then stores the result in Vx.
    */
    private void _0x7xkk(short opcode) {
        byte x;
        byte kk;
        x = ((byte) (opcode >>> 8 & 0xF));
        kk = (byte) (opcode & 0xFF);
        chip8.registers[x] += kk;
    }

    /*
        6xkk - LD Vx, byte
        Set Vx = kk.
        The interpreter puts the value kk into register Vx.
    */
    private void _0x6xkk(short opcode) {
        byte x;
        byte kk;
        x = ((byte) (opcode >>> 8 & 0xF));
        kk = (byte) (opcode & 0xFF);
        chip8.registers[x] = kk;
    }

    /*
        5xy0 - SE Vx, Vy
        Skip next instruction if Vx = Vy.
        The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
    */
    private void _0x5xy0(short opcode) {
        byte x;
        x = ((byte) (opcode >>> 8 & 0xF));
        byte y = ((byte) (opcode >>> 4 & 0xF));
        if (chip8.registers[x] == chip8.registers[y]) {
            chip8.pc += INSTRUCTION_SIZE_IN_BYTES;
        }
    }

    /*
        4xkk - SNE Vx, byte
        Skip next instruction if Vx != kk.
        The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
    */
    private void _0x4xkk(short opcode) {
        byte kk;
        byte x;
        kk = (byte) (opcode & 0xFF);
        x = ((byte) (opcode >>> 8 & 0xF));
        if (chip8.registers[x] != kk) {
            chip8.pc+= INSTRUCTION_SIZE_IN_BYTES;
        }
    }

    /*
        3xkk - SE Vx, byte
        Skip next instruction if Vx = kk.
        The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
    */
    private void _0x3xkk(short opcode) {
        byte kk = (byte) (opcode & 0xFF);
        byte x = ((byte) (opcode >>> 8 & 0xF));
        if (chip8.registers[x] == kk) {
            chip8.pc += INSTRUCTION_SIZE_IN_BYTES;
        }
    }

    /*
        2nnn - CALL addr
        Call subroutine at nnn.
        The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
    */
    private void _0x2nnn(short opcode) {
        chip8.pc += INSTRUCTION_SIZE_IN_BYTES;
        chip8.stack[chip8.stackPointer++] = chip8.pc;
        chip8.pc = (short) (opcode & 0x0FFF);
    }

    /*
        1nnn - JP addr
        Jump to location nnn.
        The interpreter sets the program counter to nnn.
    */
    private void _0x1nnn(short opcode) {
        chip8.pc = (short) (opcode & 0x0FFF);
    }

    /*
        0nnn - SYS addr
        Jump to a machine code routine at nnn.
        This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
    */
    private void _0nnn() {
        throw new UnsupportedOperationException("0NNN is unsupported on this interpreter");
    }

    /*
        00EE - RET
        Return from a subroutine.
        The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
    */
    private void _0x00EE() {
        chip8.pc = chip8.stack[--chip8.stackPointer];
    }

    /*
        00E0-CLS
        Clear the display.
    */
    private void _0x00E0() {
        chip8.clearScreen = true;
    }

    /*
        Ex9E - SKP Vx
        Skip next instruction if key with the value of Vx is pressed.
        Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2.
    */
    private void _0xEx9E(short opcode) {
        //TODO IMPLEMENTATION
        byte x = getX(opcode);
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

    private byte getNNN(short opcode) {
        return (byte) (opcode & 0xFFF);
    }
}
//
//
//        ExA1 - SKNP Vx
//        Skip next instruction if key with the value of Vx is not pressed.
//
//        Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2.
//
//
//        Fx07 - LD Vx, DT
//        Set Vx = delay timer value.
//
//        The value of DT is placed into Vx.
//
//
//        Fx0A - LD Vx, K
//        Wait for a key press, store the value of the key in Vx.
//
//        All execution stops until a key is pressed, then the value of that key is stored in Vx.
//
//
//        Fx15 - LD DT, Vx
//        Set delay timer = Vx.
//
//        DT is set equal to the value of Vx.
//
//
//        Fx18 - LD ST, Vx
//        Set sound timer = Vx.
//
//        ST is set equal to the value of Vx.
//
//
//        Fx1E - ADD I, Vx
//        Set I = I + Vx.
//
//        The values of I and Vx are added, and the results are stored in I.
//
//
//        Fx29 - LD F, Vx
//        Set I = location of sprite for digit Vx.
//
//        The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx. See section 2.4, Display, for more information on the Chip-8 hexadecimal font.
//
//
//        Fx33 - LD B, Vx
//        Store BCD representation of Vx in memory locations I, I+1, and I+2.
//
//        The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.
//
//
//        Fx55 - LD [I], Vx
//        Store registers V0 through Vx in memory starting at location I.
//
//
//        The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
//
//        Fx65 - LD Vx, [I]
//        Read registers V0 through Vx from memory starting at location I.
//
//        The interpreter reads values from memory starting at location I into registers V0 through Vx.
//
