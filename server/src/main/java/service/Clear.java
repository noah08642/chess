package service;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;

public class Clear {

    private final SQLGameDAO gdb;
    private final SQLAuthDAO adb;
    private final SQLUserDAO udb;

    public Clear(SQLGameDAO gdb, SQLAuthDAO adb, SQLUserDAO udb) {
        this.adb = adb;
        this.gdb = gdb;
        this.udb = udb;
    }

    public void clear() throws DataAccessException {
        adb.clear();
        gdb.clear();
        udb.clear();
    }
}
