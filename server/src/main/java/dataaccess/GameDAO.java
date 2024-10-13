package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.Map;


public interface GameDAO {

    void insertGame(GameData g) throws DataAccessException;
    GameData getGame(int gameID);
    boolean gameExists(GameData u);
    Map<IntegerID, GameData> listGames();
    void deleteGame(GameData u) throws DataAccessException;
    void clear();
}
//(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)