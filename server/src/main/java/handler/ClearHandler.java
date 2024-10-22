package handler;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import service.Clear;

public class ClearHandler {


    public ClearHandler(MemoryUserDAO udb, MemoryAuthDAO adb, MemoryGameDAO gdb) {
        Clear clear = new Clear(gdb, adb, udb);
        clear.clear();
    }
}
