package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {

    private SQLGameDAO gdb;

    @BeforeEach
    public void setUp() throws DataAccessException {
        this.gdb = new SQLGameDAO();
        gdb.clear();
    }

    @Test
    public void insertGood() throws DataAccessException {
        GameData g = new GameData(123, null, null,
                "awesomeGame", new ChessGame());
        gdb.insertGame(g);
        assertNotNull(g);
    }

    @Test
    public void insertBad() throws DataAccessException {
        GameData g = new GameData(123, null, null,
                "awesomeGame", new ChessGame());
        GameData g2 = new GameData(123, null, null,
                "awesomeGame", new ChessGame());
        gdb.insertGame(g);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> gdb.insertGame(g2));
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    public void getNonexistentGame() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gdb.getGame(12));
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    public void getExistingGame() throws DataAccessException {
        GameData g = new GameData(123, null, null,
                "awesomeGame", new ChessGame());
        gdb.insertGame(g);
        GameData g2 = gdb.getGame(123);

        assertNotNull(g2);
        assertEquals(g, g2);
    }



    @Test
    public void clearGood() throws DataAccessException {
        GameData g = new GameData(123, null, null,
                "awesomeGame", new ChessGame());
        GameData g2 = new GameData(1234, null, null,
                "awesomeGame2", new ChessGame());

        gdb.insertGame(g);
        gdb.insertGame(g2);

        gdb.clear();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> gdb.getGame(123));
        assertEquals("Error: unauthorized", exception.getMessage());
    }
}
