package handler;

import com.google.gson.Gson;
import dataaccess.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListRequest;
import result.CreateJoinResult;
import result.ListResult;
import service.GameService;

public class GameHandler {

    spark.Request req;
    GameService gameService;

    public GameHandler(spark.Request req, SQLUserDAO udb, SQLAuthDAO adb, SQLGameDAO gdb) {
        this.req = req;
        this.gameService = new GameService(gdb, adb, udb);
    }

    public String handleList() throws DataAccessException {
        ListRequest request = new ListRequest(req.headers("Authorization"));
        ListResult result = gameService.list(request);

        var serializer = new Gson();
        return serializer.toJson(result);
    }

    public String handleCreate() throws DataAccessException {
        String authToken = req.headers("Authorization");
        var serializer = new Gson();
        var partialRequest = serializer.fromJson(req.body(), CreateGameRequest.class);
        CreateGameRequest createRequest = new CreateGameRequest(partialRequest.gameName(), authToken);
        CreateJoinResult result = gameService.create(createRequest);

        return serializer.toJson(result);
    }

    public String handleJoin() throws DataAccessException {
        var serializer = new Gson();
        var joinRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
        joinRequest.addAuth(req.headers("Authorization"));
        CreateJoinResult result = gameService.join(joinRequest);

        return serializer.toJson(result);
    }
}
