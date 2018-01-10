import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;

public class Disassembler {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Please provide *.ch8 file");
        }

        byte[] bytes = readAllBytes(Paths.get(args[0]));

        for (int i = 0; i < bytes.length; i++) {
            decode((short) (bytes[i] << 8 | bytes[i + 1] & 0xFF), i);
            i += 2;
        }
    }
}
