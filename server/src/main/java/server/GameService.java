package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import model.AuthData;
import request.CreateGameRequest;
import result.CreateResult;

public class GameService {

    private MemoryGameDAO gdb;
    private MemoryAuthDAO adb;

    GameService(MemoryGameDAO gdb, MemoryAuthDAO adb) {
        this.adb = adb;
        this.gdb = gdb;
    }



    public CreateResult create(CreateGameRequest request) throws DataAccessException {
        String gameName = request.gameName();
        String authToken = request.authToken();

        AuthData a = adb.getAuth(authToken);
        GameData g = new GameData(1234, )

        GameData g = gdb.getGame
    }
}
