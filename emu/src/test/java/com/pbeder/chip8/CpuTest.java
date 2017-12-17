package com.pbeder.chip8;

import org.junit.Assert;
import org.junit.Test;

import static com.pbeder.chip8.Cpu.INSTRUCTION_SIZE_IN_BYTES;
import static com.pbeder.chip8.Fonts.FONT_HEIGHT;

public class CpuTest extends Chip8TestBase {

    // 0NNN  Calls RCA 1802 program at address NNN. Not necessary for most ROMs.
    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationExceptionOn_0NNN() {
        chip8.handleOpcode((short) 0x0EEE);
    }

    // 00E0	Clears the screen.
    @Test
    public void shouldSetClearScreenFlagOn_00E0() {
        // Given
        chip8.setPixel((byte) 0, (byte) 0);
        // When
        chip8.handleOpcode((short) 0x00E0);

        // Then
        Assert.assertArrayEquals(chip8.getScreen(), new boolean[32][64]);
    }

    // 00EE	Returns from a subroutine.
    @Test
    public void shouldDecreaseStackPointerAndSetProgramCounterToPreviousAddressOn_00EE() {
        //Given
        final short previousFrameAddress = 0xBCD;
        final byte expectedStackPointer = 0;
        chip8.stack[chip8.stackPointer++] = previousFrameAddress;
        //When
        chip8.handleOpcode((short) 0x00EE);
        //Then
        assertStackPointerIs(expectedStackPointer);
        assertProgramCounterIs(previousFrameAddress);
    }

    // 1NNN	Jumps to address NNN.
    @Test
    public void shouldJumpToAddressNNNOn_1NNN() {
        //Given
        final short opCodeWithJumpAddress = 0x1BAB;
        //When
        chip8.handleOpcode(opCodeWithJumpAddress);
        //Then
        assertProgramCounterIs(0xBAB);
    }

    // 2NNN	Calls subroutine at NNN.
    @Test
    public void shouldCallSubroutineAtNNNOn_2NNN() {
        //Given
        short opCodeWithJumpAddress = 0x2BAC;
        byte previousStackPointer = 5;
        short previousPC = 0x0CDE;

        chip8.stackPointer = previousStackPointer;
        chip8.pc = previousPC;
        //When
        chip8.handleOpcode(opCodeWithJumpAddress);
        //Then
        assertStackPointerIs(++previousStackPointer);
        assertTopStackIs(previousPC + INSTRUCTION_SIZE_IN_BYTES);
        assertProgramCounterIs(opCodeWithJumpAddress);
    }

    // 3XNN	Skips the next instruction if VX equals NN.
    @Test
    public void shouldSkipNextInstructionOn_3XNN() {
        //Given
        final short opCode = 0x377A;
        final byte vX = 0x7A;
        final byte x = 7;
        short pc = 0x5;

        chip8.pc = pc;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertProgramCounterIs(pc + 2 * INSTRUCTION_SIZE_IN_BYTES);
    }

    // 3XNN	Skips the next instruction if VX equals NN.
    @Test
    public void shouldNotSkipNextInstructionOn_3XNN() {
        //Given
        final short instruction = 0x37CA;
        final byte vX = (byte) (0xCB & 0xFF);
        final byte x = 7;
        short pc = 0x5;

        chip8.pc = pc;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(instruction);
        //Then
        assertProgramCounterIs(pc + INSTRUCTION_SIZE_IN_BYTES);
    }

    // 4XNN	Skips the next instruction if VX doesn't equal NN.
    @Test
    public void shouldSkipNextInstructionOn_4XNN() {
        //Given
        final short instruction = 0x47CA;
        final byte vX = (byte) 0xCF;
        final byte x = 0x7;
        short pc = 0x5;

        chip8.pc = pc;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(instruction);
        //Then
        assertProgramCounterIs(pc + 2 * INSTRUCTION_SIZE_IN_BYTES);
    }

    // 4XNN	Skips the next instruction if VX doesn't equal NN.
    @Test
    public void shouldNotSkipNextInstructionOn_4XNN() {
        //Given
        final short instruction = 0x47CA;
        final byte vX = (byte) 0xCA;
        final byte x = 0x7;
        short pc = 0x5;

        chip8.pc = pc;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(instruction);
        //Then
        assertProgramCounterIs(pc + INSTRUCTION_SIZE_IN_BYTES);
    }

    // 5XY0	Skips the next instruction if VX equals VY.
    @Test
    public void shouldSkipNextInstructionOn_5XY0() {
        //Given
        final short instruction = 0x5480;
        final byte x = 0x4;
        final byte y = 0x8;
        final byte value = 0x7C;
        final short pc = 0x10;

        chip8.registers[x] = value;
        chip8.registers[y] = value;
        chip8.pc = pc;
        //When
        chip8.handleOpcode(instruction);
        //Then
        assertProgramCounterIs(pc + 2 * INSTRUCTION_SIZE_IN_BYTES);
    }

    // 5XY0	Skips the next instruction if VX equals VY.
    @Test
    public void shouldNotSkipNextInstructionOn_5XY0() {
        //Given
        final short opCode = 0x5480;
        final int x = 0x4;
        final int y = 0x8;
        final byte vX = 0xE;
        final byte vY = 0xA;
        final short pc = 0xAFF;

        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        chip8.pc = pc;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertProgramCounterIs(pc + INSTRUCTION_SIZE_IN_BYTES);
    }

    // 6XNN	Sets VX to NN.
    @Test
    public void shouldSetVXToNNOn6XNN() {
        //Given
        final short opCode = 0x6345;
        final byte value = 0x45;
        final byte x = 0x3;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, value);
    }

    // 7XNN	Adds NN to VX.
    @Test
    public void shouldAddNNtoVXOn_7XNN() {
        //Given
        final short opCode = 0x74CD;
        final byte x = 0x4;
        final byte vX = (byte) 0xAB;
        final byte kk = (byte) 0xCD;
        final byte sum = (byte) (vX + kk);
        //When
        chip8.registers[x] = vX;
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
    }

    // 7XNN	Adds NN to VX.
    @Test
    public void shouldAddNNtoVXOn_7XNN_AS_UNSIGNED() {
        //Given
        final short opCode = 0x7404;
        final byte x = 0x4;
        final byte vX = (byte) 0xFE; //-2 or 254
        final byte kk = (byte) 0x04;
        final byte sum = (byte) 0x02;
        //When
        chip8.registers[x] = vX;
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
    }

    // 8XY0	Sets VX to the value of VY.
    @Test
    public void shouldSetVXWithXYValueOn_8XY0() {
        //Given
        final short opCode = (short) 0x8340;
        final byte x = 0x3;
        final byte y = 0x4;
        final byte vY = 0xF;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, vY);
    }

    // 8XY1	Sets VX to VX or VY.
    @Test
    public void shouldSetVxBitwiseORVyOn_8XY1() {
        //Given
        final short opCode = (short) 0x8231;
        final byte vX = (byte) 0b10110100;
        final byte vY = (byte) 0b11110101;
        final byte or = vX | vY;
        final byte x = 2;
        final byte y = 3;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, or);
    }

    // 8XY2	Sets VX to VX and VY.
    @Test
    public void shouldSetVXToVXANDVY_8XY2() {
        //Given
        final short opCode = (short) 0x8782;
        final byte vX = (byte) 0b10101010;
        final byte vY = (byte) 0b10001100;
        final byte and = vX & vY;
        final byte x = 7;
        final byte y = 8;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, and);
    }

    // 8XY3	Sets VX to VX xor VY.
    @Test
    public void shouldSetVXToVXXORVY_8XY3() {
        //Given
        final short opCode = (short) 0x80C3;
        final byte vX = (byte) 0b10101010;
        final byte vY = (byte) 0b10001100;
        final byte xor = vX ^ vY;
        final byte x = 0x0;
        final byte y = 0xC;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, xor);
    }

    // 8XY4	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
    @Test
    public void shouldNotSetCarry_8XY4() {
        //Given
        final short opCode = (short) 0x80F4;
        final byte vX = (byte) 0x64;
        final byte vY = (byte) 0x9B;
        final byte sum = (byte) (vX + vY);
        final byte x = (byte) 0x0;
        final byte y = (byte) 0xF;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsNotSet();
    }

    // 8XY4	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
    @Test
    public void shouldSetCarry_8XY4() {
        //Given
        final short opCode = (short) 0x80F4;
        final byte vX = (byte) 0xFF;
        final byte vY = (byte) 0xCD;
        final byte sum = vX + vY;
        final byte x = (byte) 0x0;
        final byte y = (byte) 0xF;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsSet();
    }

    // 8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    @Test
    public void shouldNotSetCarry_8XY5() {
        //Given
        final short opCode = (short) 0x8015;
        final byte vX = (byte) 0x15;
        final byte vY = (byte) 0x16;
        final byte sum = vX - vY;
        final byte x = (byte) 0x0;
        final byte y = (byte) 0x1;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsNotSet();
    }

    // 8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    @Test
    public void shouldSetCarry_8XY5() {
        //Given
        final short opCode = (short) 0x8015;
        final byte vX = (byte) 0x16;
        final byte vY = (byte) 0x15;
        final byte sum = vX - vY;
        final byte x = (byte) 0x0;
        final byte y = (byte) 0x1;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsSet();
    }

    // 8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    @Test
    public void shouldSetCarry_8XY5_UNSIGNED() {
        //Given
        final short opCode = (short) 0x8015;
        final byte vX = (byte) 0xFF;
        final byte vY = (byte) 0x02;
        final byte sum = (byte) 0xFD;
        final byte x = (byte) 0x0;
        final byte y = (byte) 0x1;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsSet();
    }

    // 8XY6	Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.[2]
    @Test
    public void shouldShiftRight_Carry_8XY6() {
        //Given
        final short opCode = (short) 0x85E6;
        final byte vX = (byte) 0x81;
        final byte x = (byte) 0x5;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, vX >>> 1);
        assertCarryIsSet();
    }

    // 8XY6	Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.[2]
    @Test
    public void shouldShiftRight_NoCarry_8XY6() {
        //Given
        final short opCode = (short) 0x85E6;
        final byte vX = (byte) 0x80;
        final byte x = (byte) 0x5;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, vX >>> 1);
        assertCarryIsNotSet();
    }

    // 8XY7	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    @Test
    public void shouldSubtract_NoCarry_8XY7() {
        //Given
        final short opCode = (short) 0x8AB7;
        final byte vX = (byte) 0x80;
        final byte vY = (byte) 0x01;
        final byte x = (byte) 0xA;
        final byte y = (byte) 0xB;
        final byte sum = (byte) (vY - vX);
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsNotSet();
    }

    // 8XY7	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
    @Test
    public void shouldSubtract_Carry_8XY7() {
        //Given
        final short opCode = (short) 0x8AB7;
        final byte vX = (byte) 0x01;
        final byte vY = (byte) 0x80;
        final byte x = (byte) 0xA;
        final byte y = (byte) 0xB;
        final byte sum = (byte) (vY - vX);
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, sum);
        assertCarryIsSet();
    }

    // 8XYE	Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.[2]
    @Test
    public void shouldShiftLeft_WithCarry_8XYE() {
        //Given
        final short opCode = (short) 0x8CDE;
        final byte x = (byte) 0xC;
        final byte vX = (byte) 0x88;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, vX << 1);
        assertCarryIsSet();
    }

    // 8XYE	Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.[2]
    @Test
    public void shouldShiftLeft_WithoutCarry_8XYE() {
        //Given
        final short opCode = (short) 0x8CDE;
        final byte x = (byte) 0xC;
        final byte vX = (byte) 0x78;
        chip8.registers[x] = vX;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, vX << 1);
        assertCarryIsNotSet();
    }

    // 9XY0	Skips the next instruction if VX doesn't equal VY.
    @Test
    public void shouldSkipNextInstruction_9XY0() {
        //Given
        final short opCode = (short) 0x9CD0;
        final byte x = (byte) 0xC;
        final byte y = (byte) 0xD;
        final byte vX = (byte) 0xAA;
        final byte vY = (byte) 0xAB;
        final int pc = 10;
        chip8.registers[x] = vX;
        chip8.registers[y] = vY;
        chip8.pc = pc;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertProgramCounterIs(pc + 2 * INSTRUCTION_SIZE_IN_BYTES);
    }

    // 9XY0	Skips the next instruction if VX doesn't equal VY.
    @Test
    public void shouldNotSkipNextInstruction_9XY0() {
        //Given
        final short opCode = (short) 0x9CD0;
        final byte x = (byte) 0xC;
        final byte y = (byte) 0xD;
        final byte vX = (byte) 0xAA;
        final int pc = 10;
        chip8.registers[x] = vX;
        chip8.registers[y] = vX;
        chip8.pc = pc;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertProgramCounterIs(pc + INSTRUCTION_SIZE_IN_BYTES);
    }

    // ANNN	Sets I to the address NNN.
    @Test
    public void shouldSetIToNNN() {
        //Given
        final short opCode = (short) 0xA123;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertIIs(0x123);
    }

    // BNNN	Jumps to the address NNN plus V0.
    @Test
    public void shouldJumpToNNNPlusV0() {
        //Given
        final short opCode = (short) 0xB234;
        chip8.pc = (short) 0x0011;
        chip8.registers[0] = (byte) 0x0A;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertProgramCounterIs(0xB234 + 0x0A);
    }

    // CXNN	Sets VX to the result of a bitwise and operation on a random number and NN.
    @Test
    public void shouldGenerateRandomAndBitwiseAndWithNNAndSetVxWithValue() {
        //Given
        final short opCode = (short) 0xC5A2;
        final byte x = 0x5;
        chip8.setRandomGenerator(() -> (byte) 0x0A);
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertThatRegisterXIs(x, 0xA2 & 0x0A);
    }

    // DXYN	Sprites stored in memory at location in index register (I), 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero.
    // All drawing is XOR drawing (i.e. it toggles the screen pixels).
    // Sprites are drawn starting at position VX, VY. N is the number of 8bit rows that need to be drawn. If N is greater than 1, second line continues at position VX, VY+1, and so on.
    @Test
    public void shouldDraw2x2RectangleInUpperRightCorner() {
        //Given
        boolean[][] screenConfiguration = new boolean[32][64];
        screenConfiguration[0][62] = true;
        screenConfiguration[0][63] = true;
        screenConfiguration[1][62] = true;
        screenConfiguration[1][63] = true;
        final short someMemoryAddress = 0x0ABC;
        chip8.I = someMemoryAddress;
        chip8.memory[someMemoryAddress] = 0b00000011;
        chip8.memory[someMemoryAddress + 1] = 0b00000011;
        final short opCode = (short) 0xD3B2;
        final byte x = (opCode >> 8) & 0xF;
        final byte y = (opCode >> 4) & 0xF;
        chip8.registers[x] = 56;
        chip8.registers[y] = 0;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertCarryIsNotSet();
        assertScreenConfigurationIs(screenConfiguration);
    }

    // DXYN	Sprites stored in memory at location in index register (I), 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero.
    // All drawing is XOR drawing (i.e. it toggles the screen pixels).
    // Sprites are drawn starting at position VX, VY. N is the number of 8bit rows that need to be drawn. If N is greater than 1, second line continues at position VX, VY+1, and so on.
    @Test
    public void shouldDraw2x2RectangleInUpperRightCornerWrapped() {
        //Given
        boolean[][] screenConfiguration = new boolean[32][64];
        screenConfiguration[0][0] = true;
        screenConfiguration[0][63] = true;
        screenConfiguration[1][0] = true;
        screenConfiguration[1][63] = true;
        final short someMemoryAddress = 0x0ABC;
        chip8.I = someMemoryAddress;
        chip8.memory[someMemoryAddress] = 0b00000011;
        chip8.memory[someMemoryAddress + 1] = 0b00000011;
        final short opCode = (short) 0xD3B2;
        final byte x = (opCode >> 8) & 0xF;
        final byte y = (opCode >> 4) & 0xF;
        chip8.registers[x] = 57;
        chip8.registers[y] = 0;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertCarryIsNotSet();
        assertScreenConfigurationIs(screenConfiguration);
    }

    // DXYN	Sprites stored in memory at location in index register (I), 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero.
    // All drawing is XOR drawing (i.e. it toggles the screen pixels).
    // Sprites are drawn starting at position VX, VY. N is the number of 8bit rows that need to be drawn. If N is greater than 1, second line continues at position VX, VY+1, and so on.
    @Test
    public void shouldDraw2x2RectangleInLowerRightCornerWrapped() {
        //Given
        boolean[][] screenConfiguration = new boolean[32][64];
        screenConfiguration[31][62] = true;
        screenConfiguration[31][63] = true;
        screenConfiguration[0][62] = true;
        screenConfiguration[0][63] = true;
        final short someMemoryAddress = 0x0ABC;
        chip8.I = someMemoryAddress;
        chip8.memory[someMemoryAddress] = 0b00000011;
        chip8.memory[someMemoryAddress + 1] = 0b00000011;
        final short opCode = (short) 0xD3B2;
        final byte x = (opCode >> 8) & 0xF;
        final byte y = (opCode >> 4) & 0xF;
        chip8.registers[x] = 56;
        chip8.registers[y] = 31;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertCarryIsNotSet();
        assertScreenConfigurationIs(screenConfiguration);
    }

    // DXYN	Sprites stored in memory at location in index register (I), 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero.
    // All drawing is XOR drawing (i.e. it toggles the screen pixels).
    // Sprites are drawn starting at position VX, VY. N is the number of 8bit rows that need to be drawn. If N is greater than 1, second line continues at position VX, VY+1, and so on.
    @Test
    public void shouldDraw2x2RectangleInLowerRightCornerWrappedOnXAndYAxis() {
        //Given
        boolean[][] screenConfiguration = new boolean[32][64];
        screenConfiguration[31][63] = true;
        screenConfiguration[31][0] = true;
        screenConfiguration[0][63] = true;
        screenConfiguration[0][0] = true;
        final short someMemoryAddress = 0x0ABC;
        chip8.I = someMemoryAddress;
        chip8.memory[someMemoryAddress] = 0b00000011;
        chip8.memory[someMemoryAddress + 1] = 0b00000011;
        final short opCode = (short) 0xD3B2;
        final byte x = (opCode >> 8) & 0xF;
        final byte y = (opCode >> 4) & 0xF;
        chip8.registers[x] = 57;
        chip8.registers[y] = 31;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertCarryIsNotSet();
        assertScreenConfigurationIs(screenConfiguration);
    }

    // DXYN	Sprites stored in memory at location in index register (I), 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero.
    // All drawing is XOR drawing (i.e. it toggles the screen pixels).
    // Sprites are drawn starting at position VX, VY. N is the number of 8bit rows that need to be drawn. If N is greater than 1, second line continues at position VX, VY+1, and so on.
    @Test
    public void shouldDetectCollisionAndFadePixel() {
        //Given
        final short opCode = (short) 0xD3B2;
        final byte vx = (opCode >> 8) & 0xF;
        final byte vy = (opCode >> 4) & 0xF;
        boolean[][] screenConfiguration = new boolean[32][64];
        byte x = 56;
        byte y = 24;
        screenConfiguration[y][x] = false;
        chip8.setPixel(x, y);
        final short someMemoryAddress = 0x0ABC;
        chip8.I = someMemoryAddress;
        chip8.memory[someMemoryAddress] = (byte) 0b10000000;
        chip8.registers[vx] = x;
        chip8.registers[vy] = y;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertCarryIsSet();
        assertScreenConfigurationIs(screenConfiguration);
    }

    // EX9E	Skips the next instruction if the key stored in VX is pressed.
    // EXA1	Skips the next instruction if the key stored in VX isn't pressed.
    // FX07	Sets VX to the value of the delay timer.
    // FX0A	A key press is awaited, and then stored in VX.
    // FX15	Sets the delay timer to VX.
    // FX18	Sets the sound timer to VX.

    // FX1E	Adds VX to I.[3]
    @Test
    public void shouldSumVxAndI() throws Exception {
        //Given
        byte x = 5;
        byte vx = 0xA;
        short opCode = (short) 0xF51E;
        final short someMemoryAddress = 0x0ABC;
        chip8.I = someMemoryAddress;
        chip8.registers[x] = vx;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertIIs(someMemoryAddress + vx);
    }

    // FX29	Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
    @Test
    public void shouldSetIToFont6Place() throws Exception {
        //Given
        byte x = 7;
        byte six = 6;
        short opCode = (short) 0xF729;
        chip8.registers[x] = six;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertIIs(six * FONT_HEIGHT);
    }

    // FX33	Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
    @Test
    public void shouldConvert247InVxToBCD() throws Exception {
        //Given
        short opCode = (short) 0xF333;
        chip8.I = 0x200;
        chip8.registers[3] = (byte) 247;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertMemoryIs((short) 0x200, (byte) 0x2);
        assertMemoryIs((short) 0x201, (byte) 0x4);
        assertMemoryIs((short) 0x202, (byte) 0x7);
    }

    // FX33	Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
    @Test
    public void shouldConvert255InVxToBCD() throws Exception {
        //Given
        short opCode = (short) 0xF333;
        chip8.I = 0x200;
        chip8.registers[3] = (byte) 255;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertMemoryIs((short) 0x200, (byte) 0x2);
        assertMemoryIs((short) 0x201, (byte) 0x5);
        assertMemoryIs((short) 0x202, (byte) 0x5);
    }

    // FX33	Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
    @Test
    public void shouldConvert0InVxToBCD() throws Exception {
        //Given
        short opCode = (short) 0xF333;
        chip8.I = 0x200;
        chip8.registers[3] = (byte) 0;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertMemoryIs((short) 0x200, (byte) 0x0);
        assertMemoryIs((short) 0x201, (byte) 0x0);
        assertMemoryIs((short) 0x202, (byte) 0x0);
    }

    // FX33	Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
    @Test
    public void shouldConvert9InVxToBCD() throws Exception {
        //Given
        short opCode = (short) 0xF333;
        chip8.I = 0x200;
        chip8.registers[3] = (byte) 9;
        //When
        chip8.handleOpcode(opCode);
        //Then
        assertMemoryIs((short) 0x200, (byte) 0x0);
        assertMemoryIs((short) 0x201, (byte) 0x0);
        assertMemoryIs((short) 0x202, (byte) 0x9);
    }

    // FX55	Stores V0 to VX (including VX) in memory starting at address I.[4]
    @Test
    public void shouldCopyRegistersToMemory() throws Exception {
        //Given
        byte v0 = 0xA;
        byte v1 = 0x7;
        byte v2 = 0xF;
        byte v3 = 0x0;
        byte v4 = 0xB;
        chip8.I = 0x200;
        chip8.registers[0] = v0;
        chip8.registers[1] = v1;
        chip8.registers[2] = v2;
        chip8.registers[3] = v3;
        chip8.registers[4] = v4;
        short opcode = (short) 0xF455;
        //When
        chip8.handleOpcode(opcode);
        //Then
        assertMemoryIs(chip8.I, v0);
        assertMemoryIs((short) (chip8.I + 1), v1);
        assertMemoryIs((short) (chip8.I + 2), v2);
        assertMemoryIs((short) (chip8.I + 3), v3);
        assertMemoryIs((short) (chip8.I + 4), v4);
    }

    // FX65	Fills V0 to VX (including VX) with values from memory starting at address I.[4]
    @Test
    public void shouldFromMemoryToRegisters() throws Exception {
        //Given
        byte v0 = 0xA;
        byte v1 = 0x7;
        byte v2 = 0xF;
        byte v3 = 0x0;
        byte v4 = 0xB;
        chip8.I = 0x200;
        chip8.memory[chip8.I] = v0;
        chip8.memory[chip8.I + 1] = v1;
        chip8.memory[chip8.I + 2] = v2;
        chip8.memory[chip8.I + 3] = v3;
        chip8.memory[chip8.I + 4] = v4;
        short opcode = (short) 0xF465;
        //When
        chip8.handleOpcode(opcode);
        //Then
        assertThatRegisterXIs((byte) 0, v0);
        assertThatRegisterXIs((byte) 1, v1);
        assertThatRegisterXIs((byte) 2, v2);
        assertThatRegisterXIs((byte) 3, v3);
        assertThatRegisterXIs((byte) 4, v4);
    }
}
