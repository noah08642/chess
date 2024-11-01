package service;

import service.Clear;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearTest {


    @Test
    public void clearPositive() throws DataAccessException {
        SQLUserDAO udb = new SQLUserDAO();
        SQLAuthDAO adb = new SQLAuthDAO();
        SQLGameDAO gdb = new SQLGameDAO();

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
