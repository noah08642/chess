package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void addPlayerGood() throws DataAccessException {
        GameData g = new GameData(123, null, null,
                "awesomeGame", new ChessGame());
        gdb.insertGame(g);

        gdb.addPlayer(ChessGame.TeamColor.BLACK, 123, "poopy");

        GameData retrievedGame = gdb.getGame(123);
        assertNotNull(retrievedGame);

        // Verify that the BLACK player was added with the expected username.
        assertEquals("poopy", retrievedGame.getPlayerName(ChessGame.TeamColor.BLACK));

    }

    @Test
    public void addPlayerBad() throws DataAccessException {
        GameData g = new GameData(123, null, null, "awesomeGame", new ChessGame());
        gdb.insertGame(g);
        gdb.addPlayer(ChessGame.TeamColor.BLACK, 123, "poopy");

        // Try to add another black user
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gdb.addPlayer(ChessGame.TeamColor.BLACK, 123, "anotherPlayer")
        );
        assertEquals("Error: already taken", exception.getMessage());

        // Attempting to add a player to a non-existent game (e.g., game ID 999) should throw an exception.
        DataAccessException nonExistentException = assertThrows(DataAccessException.class, () ->
                gdb.addPlayer(ChessGame.TeamColor.WHITE, 999, "newPlayer")
        );
        assertEquals("Error: unauthorized", nonExistentException.getMessage());
    }

    @Test
    public void listGamesGood() throws DataAccessException {

        GameData g1 = new GameData(123, "whitePlayer1", "blackPlayer1", "game1", new ChessGame());
        GameData g2 = new GameData(124, "whitePlayer2", "blackPlayer2", "game2", new ChessGame());
        gdb.insertGame(g1);
        gdb.insertGame(g2);

        // Retrieve the list of games
        List<GameData> games = gdb.listGames();

        // Check that the list contains exactly two games
        assertNotNull(games);
        assertEquals(2, games.size());

        // Verify that the games match the inserted data
        assertTrue(games.contains(g1));
        assertTrue(games.contains(g2));
    }


    @Test
    public void listGamesBad() {

        // Attempt to list games from an empty database
        List<GameData> games = assertDoesNotThrow(() -> gdb.listGames());

        // Verify that the returned list is empty
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }


}
