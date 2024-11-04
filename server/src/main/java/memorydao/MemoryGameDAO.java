package memorydao;

import chess.ChessGame;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {


    private final Map<Integer, GameData> gameDatabase;

    public MemoryGameDAO() {
        gameDatabase = new HashMap<>();
    }

    public void insertGame(GameData g) throws DataAccessException {
        gameDatabase.put(g.gameID(), g);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        throwExIfInvalid(gameID);
        return gameDatabase.get(gameID);
    }

    public List<GameData> listGames() {
        return new ArrayList<>(gameDatabase.values());
    }

    public boolean gameExists(int id) {
        return gameDatabase.containsKey(id);
    }

    public void addPlayer(ChessGame.TeamColor playerColor, int gameID, String user) {
        GameData g = gameDatabase.get(gameID);
        g.addUser(playerColor, user);
    }

    public void clear() {
        gameDatabase.clear();
    }

    public void throwExIfInvalid(int gameID) throws DataAccessException {
        if (!gameDatabase.containsKey(gameID)) {
            throw new BadRequestException();
        }
    }


}
