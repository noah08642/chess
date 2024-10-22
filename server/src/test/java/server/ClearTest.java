package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearTest {


    @Test
    public void clearPositive() throws DataAccessException {
        MemoryUserDAO udb = new MemoryUserDAO();
        MemoryAuthDAO adb = new MemoryAuthDAO();
        MemoryGameDAO gdb = new MemoryGameDAO();

        udb.insertUser(new UserData("username", "password", "email"));
        adb.insertAuth(new AuthData("AuthToken", "username"));
        gdb.insertGame(new GameData(123232, "", "", "game1", new ChessGame()));

        Clear clear = new Clear(gdb, adb, udb);
        clear.clear();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> udb.getUser("username"));
        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> adb.getAuth("AuthToken"));
        DataAccessException exception3 = assertThrows(DataAccessException.class, () -> gdb.getGame(123232));
    }
}
