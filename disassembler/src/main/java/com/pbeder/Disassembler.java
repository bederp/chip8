package com.pbeder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;

public class Disassembler {
    private static final Decoder decoder = new Decoder();
    private static final AsciiDecoder ascii = new AsciiDecoder();

    public static void main(String[] args) throws IOException {
        validateInput(args);
        byte[] bytes = readFile(args[0]);
        iterateBytes(bytes);
    }

    private static byte[] readFile(String arg) throws IOException {
        String absolutePath = new File(arg).getAbsolutePath();
        return readAllBytes(Paths.get(absolutePath));
    }

    private static void iterateBytes(byte[] bytes) {
        long startAdress = 0x200L;
        for (int i = 0; i < bytes.length - 1; i += 2) {
            short opcode = (short) (bytes[i] << 8 | bytes[i + 1] & 0xFF);
            String decoded = decoder.decode(opcode);
            String ascii = Disassembler.ascii.decode(opcode);
            System.out.println(String.format("%03x: %04x %-16s %s", startAdress+i, opcode, decoded, ascii));
        }
    }

    private static void validateInput(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide *.ch8 file");
            System.exit(0);
        }
    }
}
