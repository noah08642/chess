package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListRequest;
import request.RegisterRequest;
import result.CreateJoinResult;
import result.ListResult;
import result.LogRegResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private String authToken;


    @BeforeEach
    public void setUp() throws DataAccessException {
        MemoryUserDAO udb = new MemoryUserDAO();
        MemoryAuthDAO adb = new MemoryAuthDAO();
        MemoryGameDAO gdb = new MemoryGameDAO();
        this.gameService = new GameService(gdb, adb, udb);

        UserService userService = new UserService(udb, adb);

        RegisterRequest request = new RegisterRequest("Luke", "Password", "luke@gmail.com");
        LogRegResult result = userService.register(request);
        authToken = result.authToken();
    }

    @Test
    public void createPositive() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("game1", authToken);
        CreateJoinResult result = gameService.create(request);

        assertNotNull(result, "Result should not be null");

        assertTrue(result.gameID() > 0, "Game ID should be greater than 0");
    }


    @Test
    public void createNegative() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("game1", "BadAuthToken");

        // register with invalid authToken
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.create(request));
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void joinPositive() throws DataAccessException {
        CreateGameRequest createRequest = new CreateGameRequest("game1", authToken);
        CreateJoinResult createResult = gameService.create(createRequest);

        // join game
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, createResult.gameID(), authToken);
        CreateJoinResult joinResult = gameService.join(request);
        assertEquals(joinResult.gameID(), request.gameID());
    }

    @Test
    public void joinNegative() throws DataAccessException {
        CreateGameRequest createRequest = new CreateGameRequest("game1", authToken);
        CreateJoinResult createResult = gameService.create(createRequest);

        // join game
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, createResult.gameID(), authToken);
        CreateJoinResult joinResult = gameService.join(request);

        // join game with same color
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.join(request));
        assertEquals("Color already taken", exception.getMessage());
    }

    @Test
    public void listPositive() throws DataAccessException {
        // Create a game
        CreateGameRequest request = new CreateGameRequest("game1", authToken);
        gameService.create(request);

        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = gameService.list(listRequest);
        assertEquals(listResult.games().getFirst().gameName(), "game1");
    }

    @Test
    public void listNegative() throws DataAccessException {
        // Create a game
        CreateGameRequest request = new CreateGameRequest("game1", authToken);
        gameService.create(request);

        // request with invalid authToken
        ListRequest listRequest = new ListRequest("BadToken");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.list(listRequest));
        assertEquals("Error: unauthorized", exception.getMessage());
    }


}
