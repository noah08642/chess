package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {


    private Map<IntegerID, GameData> gameDatabase;

    public MemoryGameDAO() {
        gameDatabase = new HashMap<>();
    }

    public void insertGame(GameData g) throws DataAccessException {
        IntegerID id = new IntegerID(g.gameID());
        gameDatabase.put(id, g);
    }

    public GameData getGame(int gameID) {
        IntegerID id = new IntegerID(gameID);
        return gameDatabase.get(id);
    }

    public boolean gameExists(GameData g) {
        IntegerID id = new IntegerID(g.gameID());
        return gameDatabase.containsKey(id);
    }

    public Map<IntegerID, GameData> listGames() {
        // not sure how I'm supposed to return this... as a list?
        return gameDatabase;
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
            IntegerID id = new IntegerID(g.gameID());
            gameDatabase.remove(id);
        }
    }

    public void clear() {
        gameDatabase.clear();
    }
    
    
    
    
}
