package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class Clear {

    private MemoryGameDAO gdb;
    private MemoryAuthDAO adb;
    private MemoryUserDAO udb;

    public Clear(MemoryGameDAO gdb, MemoryAuthDAO adb, MemoryUserDAO udb) {
        this.adb = adb;
        this.gdb = gdb;
        this.udb = udb;
    }

    public void clear() {
        adb.clear();
        gdb.clear();
        udb.clear();
    }
}
