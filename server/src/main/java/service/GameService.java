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

    private final SQLGameDAO gdb;
    private final SQLAuthDAO adb;
    private final SQLUserDAO udb;

    public GameService(SQLGameDAO gdb, SQLAuthDAO adb, SQLUserDAO udb) {
        this.adb = adb;
        this.gdb = gdb;
        this.udb = udb;
    }


    public CreateJoinResult create(CreateGameRequest request) throws DataAccessException {
        String gameName = request.gameName();
        String authToken = request.authToken();

        if (!adb.authExists(authToken)) {
            throw new InvalidAuthException();
        }

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

        if (!gdb.gameExists(id)) {
            throw new BadRequestException();
        }

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

        if (!adb.authExists(auth)) {
            throw new InvalidAuthException();
        }

        return new ListResult(gdb.listGames());
    }

    private int generateID() throws DataAccessException {
        GameIdGenerator gen = new GameIdGenerator();
        return gen.generate(gdb);
    }
}
