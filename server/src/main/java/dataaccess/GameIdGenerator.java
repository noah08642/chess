package dataaccess;

import java.security.SecureRandom;


public class GameIdGenerator {
    public int generate(MemoryGameDAO gdb) {
        SecureRandom num = new SecureRandom();
        int id = num.nextInt(1_000_000);
        while (gdb.gameExists(id)) {
            id = num.nextInt(1_000_000);
        }
        return id;
    }
}
