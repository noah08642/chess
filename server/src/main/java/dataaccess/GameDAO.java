package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;



public interface GameDAO {

    void insertGame(GameData g) throws DataAccessException;
    GameData getGame(int gameID);
    boolean gameExists(GameData u);
    List<GameData> listGames();
    void deleteGame(GameData u) throws DataAccessException;
    void clear();
    void addPlayer(ChessGame.TeamColor playerColor, int gameID, String user);
}
//(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)