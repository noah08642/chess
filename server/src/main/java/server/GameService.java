package server;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.AuthData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListRequest;
import result.CreateJoinResult;
import result.ListResult;

public class GameService {

    private MemoryGameDAO gdb;
    private MemoryAuthDAO adb;
    private MemoryUserDAO udb;

    GameService(MemoryGameDAO gdb, MemoryAuthDAO adb, MemoryUserDAO udb) {
        this.adb = adb;
        this.gdb = gdb;
        this.udb = udb;
    }



    public CreateJoinResult create(CreateGameRequest request) throws DataAccessException {
        String gameName = request.gameName();
        String authToken = request.authToken();

        // authenticate, generate id, add to database
        AuthData a = adb.getAuth(authToken);
        int id = generateID();
        GameData g = new GameData(id, "", "", gameName, new ChessGame());
        gdb.insertGame(g);

        // create and return returnObject
        return new CreateJoinResult(id);
    }

    public CreateJoinResult join(JoinGameRequest request) throws DataAccessException {
        ChessGame.TeamColor color = request.playerColor();
        int id = request.gameID();
        String auth = request.authToken();

        AuthData a = adb.getAuth(auth);
        String user = a.username();
        UserData u = udb.getUser(user);

        GameData g = gdb.getGame(id);
        gdb.addPlayer(color, id, user);

        return new CreateJoinResult(id);
    }

    public ListResult list(ListRequest request) throws DataAccessException {
        String auth = request.authToken();

        AuthData a = adb.getAuth(auth);
        gdb.listGames();
        // not sure how games should be passed... as a list or map?
    }



    public int generateID() {
        GameIdGenerator gen = new GameIdGenerator();
        return gen.generate(gdb);
    }
}
