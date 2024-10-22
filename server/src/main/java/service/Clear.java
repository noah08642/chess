package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class Clear {

    private final MemoryGameDAO gdb;
    private final MemoryAuthDAO adb;
    private final MemoryUserDAO udb;

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
