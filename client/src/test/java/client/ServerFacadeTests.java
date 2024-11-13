package client;

import chess.ChessGame;
import model.GameData;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import request.*;
import result.LogRegResult;
import server.Server;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() throws Exception {
        facade.clear("validAuthToken");
        server.stop();
    }

    // Positive and Negative test cases for register()
    @Test
    void register_validData() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("d", "e", "f"));
        assertNotNull(result.authToken());
    }

    @Test
    void register_invalidData() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("lol", "lol", "lol"));
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest("lol", "lol", "lol")));
    }

    // Positive and Negative test cases for login()
    @Test
    void login_validCredentials() throws Exception {
        LogRegResult result  = facade.register(new RegisterRequest("a", "b", "c"));
        assertNotNull(result.authToken());
    }

    @Test
    void login_invalidCredentials() {
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("bad", "bad")));
    }

    // Positive and Negative test cases for listGames()
    @Test
    void listGames_validToken() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("z","z","z"));
        String auth = result.authToken();
        facade.createGame(new CreateGameRequest("gamey", auth));
        var request = new ListRequest(auth);
        List<GameData> games = facade.listGames(request);
        assertNotNull(games);
    }

    @Test
    void listGames_invalidToken() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("y","y","y"));
        String auth = result.authToken();
        facade.createGame(new CreateGameRequest("gamey2", auth));

        assertThrows(Exception.class, () -> facade.listGames(new ListRequest("invalidAuthToken")));
    }

    // Positive and Negative test cases for createGame()
    @Test
    void createGame_validData() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("g", "h", "i"));
        var request = new CreateGameRequest("New Game", result.authToken());
        facade.createGame(request);
        assertTrue(true); // Success if no exception is thrown
    }

    @Test
    void createGame_invalidToken() {
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("New Game", "invalidAuthToken")));
    }

    // Positive and Negative test cases for joinGame()
    @Test
    void joinGame_validData() throws Exception {
        LogRegResult a = facade.register(new RegisterRequest("j", "k", "l"));
        facade.clear(a.authToken());
        LogRegResult result = facade.register(new RegisterRequest("j", "k", "l"));
        facade.createGame(new CreateGameRequest("gammmmme", result.authToken()));
        int id = server.returnGameDAO().listGames().getFirst().gameID();
        var request = new JoinGameRequest(ChessGame.TeamColor.WHITE, id, result.authToken());
        facade.joinGame(request);
        assertTrue(true); // Success if no exception is thrown
    }

    @Test
    void joinGame_invalidAuth() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("m", "m", "m"));
        assertThrows(Exception.class, () -> facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, -1, result.authToken())));
    }

    // Positive and Negative test cases for logout()
    @Test
    void logout_validToken() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("o", "o", "o"));
        var request = new LogoutRequest(result.authToken());
        facade.logout(request);
        assertTrue(true); // Success if no exception is thrown
    }

    @Test
    void logout_invalidToken() {
        assertThrows(Exception.class, () -> facade.logout(new LogoutRequest("invalidAuthToken")));
    }


}
