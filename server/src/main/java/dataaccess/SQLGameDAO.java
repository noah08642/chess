package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {


    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    public void insertGame(GameData g) throws DataAccessException {
        if (gameExists(g.gameID())) {
            throw new AlreadyTakenException();
        }

        // Insert the new game into the database
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, jsonGame) VALUES (?, ?, ?, ?, ?)";
        var json = new Gson().toJson(g.getGame());
        executeUpdate(statement, g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), json);
    }


    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonGame FROM game WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);  // Return the GameData object if found
                    } else {
                        throw new InvalidAuthException();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }


    public void clear() throws DataAccessException {
        var statement = "DELETE FROM game";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();  // Execute the delete statement to clear the table

        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear user table: " + e.getMessage());
        }
    }

    public void addPlayer(ChessGame.TeamColor playerColor, int gameID, String user) throws DataAccessException {
        if (!gameExists(gameID)) {
            throw new InvalidAuthException();
        }

        if ((playerColor== ChessGame.TeamColor.BLACK && (getGame(gameID).getPlayerName(ChessGame.TeamColor.BLACK) != null)) ||
                (playerColor== ChessGame.TeamColor.WHITE && (getGame(gameID).getPlayerName(ChessGame.TeamColor.WHITE) != null))) {
            throw new AlreadyTakenException();
        }

        if (playerColor == ChessGame.TeamColor.WHITE) {
            var statement = "UPDATE game SET whiteUsername = ? WHERE gameID = ?";
            executeUpdate(statement, user, gameID);
        }
        else {
            var statement = "UPDATE game SET blackUsername = ? WHERE gameID = ?";
            executeUpdate(statement, user, gameID);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonGame FROM game";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                games.add(readGame(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return games;
    }




    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var jsonGame = rs.getString("jsonGame");
        var serializer = new Gson();
        var gameObject = serializer.fromJson(jsonGame, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
    }


    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }


    public boolean gameExists(int id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT 1 FROM game WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if game exists: " + e.getMessage());
        }
    }
}
