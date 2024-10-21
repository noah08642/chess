package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import request.*;
import result.CreateJoinResult;
import result.ListResult;
import server.GameService;

public class GameHandler {

    spark.Request req;
    GameService gameService;

    public GameHandler(spark.Request req, MemoryUserDAO udb, MemoryAuthDAO adb, MemoryGameDAO gdb) {
        this.req = req;
        this.gameService = new GameService(gdb, adb, udb);
    }

    public String handleList() throws DataAccessException {
        var serializer = new Gson();
        var listRequest = serializer.fromJson(req.body(), ListRequest.class);
        ListResult result = gameService.list(listRequest);

        return serializer.toJson(result);
    }

    public String handleCreate() throws DataAccessException {
        var serializer = new Gson();
        var createRequest = serializer.fromJson(req.body(), CreateGameRequest.class);
        CreateJoinResult result = gameService.create(createRequest);

        return serializer.toJson(result);
    }

    public String handleJoin() throws DataAccessException {
        var serializer = new Gson();
        var joinRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
        CreateJoinResult result = gameService.join(joinRequest);

        return serializer.toJson(result);
    }
}
