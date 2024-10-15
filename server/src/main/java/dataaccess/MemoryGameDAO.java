package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {


    private Map<Integer, GameData> gameDatabase;

    public MemoryGameDAO() {
        gameDatabase = new HashMap<>();
    }

    public void insertGame(GameData g) throws DataAccessException {
        gameDatabase.put(g.gameID(), g);
    }

    public GameData getGame(int gameID) {
        return gameDatabase.get(gameID);
    }

    public boolean gameExists(GameData g) {
        return gameDatabase.containsKey(g.gameID());
    }

    public boolean gameExists(int gameID) {
        return gameDatabase.containsKey(gameID);
    }



    public List<GameData> listGames() {
        return new ArrayList<>(gameDatabase.values());
    }

    public void updateGame(int gameID, String newName) {
        // the spec wasn't super clear... This might need change the actual chessboard.
        GameData game = getGame(gameID);
        game.changeGameName(newName);
    }

    public void deleteGame(GameData g) throws DataAccessException {
        if(!gameExists(g)) {
            throw new DataAccessException("user doesn't exist");
        }
        else {
            gameDatabase.remove(g.gameID());
        }
    }

    public void addPlayer(ChessGame.TeamColor playerColor, int gameID, String user) {
        GameData g = gameDatabase.get(gameID);
        g.addUser(playerColor, user);
    }

    public void clear() {
        gameDatabase.clear();
    }
    
    
    
    
}
