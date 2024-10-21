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
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateJoinResult;
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

        assertNotNull(result);
    }


    @Test
    public void createNegative() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("game1", authToken);
        CreateJoinResult result = gameService.create(request);

        // register again with same name
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.create(request));
        assertEquals("Game name taken", exception.getMessage());
    }

    @Test
    public void joinPositive() throws DataAccessException {
        CreateGameRequest createRequest = new CreateGameRequest("game1", authToken);
        CreateJoinResult result = gameService.create(createRequest);

        // join game
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, )


    }

    @Test
    public void loginNegative() throws DataAccessException {
        // register a new user
        RegisterRequest regRequest = new RegisterRequest("noah", "poop", "noah@gmail.com");
        userService.register(regRequest);

        // test login with incorrect password
        LoginRequest request = new LoginRequest("noah", "poopy");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> userService.login(request));
        assertEquals("Incorrect username or password", exception.getMessage());

        // test login with incorrect user
        LoginRequest request2 = new LoginRequest("noahGoo", "poop");
        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> userService.login(request2));
        assertEquals("Incorrect username or password", exception.getMessage());
    }
}
