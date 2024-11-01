package handler;

import dataaccess.*;
import service.Clear;

public class ClearHandler {


    public ClearHandler(SQLUserDAO udb, SQLAuthDAO adb, SQLGameDAO gdb) throws DataAccessException {
        Clear clear = new Clear(gdb, adb, udb);
        clear.clear();
    }
}
