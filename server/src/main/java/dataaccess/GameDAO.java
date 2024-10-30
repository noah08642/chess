package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;


public interface GameDAO {

    void insertGame(GameData g) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;

    void addPlayer(ChessGame.TeamColor playerColor, int gameID, String user) throws DataAccessException;}
//(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)