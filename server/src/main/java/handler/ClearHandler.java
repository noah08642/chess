package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LogRegResult;
import server.Clear;
import server.GameService;
import server.UserService;

public class ClearHandler {



    public ClearHandler(MemoryUserDAO udb, MemoryAuthDAO adb, MemoryGameDAO gdb) {
        Clear clear = new Clear(gdb, adb, udb);
        clear.clear();
    }
}
