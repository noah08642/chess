package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListRequest;
import result.CreateJoinResult;
import result.ListResult;

import java.util.Objects;

public class GameService {

    private final MemoryGameDAO gdb;
    private final MemoryAuthDAO adb;
    private final MemoryUserDAO udb;

    public GameService(MemoryGameDAO gdb, MemoryAuthDAO adb, MemoryUserDAO udb) {
        this.adb = adb;
        this.gdb = gdb;
        this.udb = udb;
    }


    public CreateJoinResult create(CreateGameRequest request) throws DataAccessException {
        String gameName = request.gameName();
        String authToken = request.authToken();

        // authenticate, generate id, add to database
        adb.throwExIfInvalid(authToken);
        int id = generateID();
        GameData g = new GameData(id, null, null, gameName, new ChessGame());
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

        if (color == null) {
            throw new BadRequestException();
        }
        // throw error if color is already taken
        if (color == ChessGame.TeamColor.WHITE) {
            if (!Objects.equals(g.whiteUsername(), null)) {
                throw new AlreadyTakenException();
            }
        }
        else {
            if (!Objects.equals(g.blackUsername(), null)) {
                throw new AlreadyTakenException();
            }
        }

        gdb.addPlayer(color, id, user);

        return new CreateJoinResult(id);
    }

    public ListResult list(ListRequest request) throws DataAccessException {
        String auth = request.authToken();
        adb.throwExIfInvalid(auth);

        return new ListResult(gdb.listGames());
    }

    private int generateID() {
        GameIdGenerator gen = new GameIdGenerator();
        return gen.generate(gdb);
    }
}
