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
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() throws Exception {
        facade.clear("validAuthToken");
        server.stop();
    }

    // Positive and Negative test cases for register()
    @Test
    void registerGood() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("d", "e", "f"));
        assertNotNull(result.authToken());
    }

    @Test
    void registerBad() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("lol", "lol", "lol"));
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest("lol", "lol", "lol")));
    }

    // Positive and Negative test cases for login()
    @Test
    void loginGood() throws Exception {
        LogRegResult result  = facade.register(new RegisterRequest("a", "b", "c"));
        assertNotNull(result.authToken());
    }

    @Test
    void loginBad() {
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("bad", "bad")));
    }

    // Positive and Negative test cases for listGames()
    @Test
    void listGamesGood() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("z","z","z"));
        String auth = result.authToken();
        facade.createGame(new CreateGameRequest("gamey", auth));
        var request = new ListRequest(auth);
        List<GameData> games = facade.listGames(request);
        assertNotNull(games);
    }

    @Test
    void listGamesBad() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("y","y","y"));
        String auth = result.authToken();
        facade.createGame(new CreateGameRequest("gamey2", auth));

        assertThrows(Exception.class, () -> facade.listGames(new ListRequest("invalidAuthToken")));
    }

    // Positive and Negative test cases for createGame()
    @Test
    void createGameGood() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("g", "h", "i"));
        var request = new CreateGameRequest("New Game", result.authToken());
        facade.createGame(request);
        assertTrue(true); // Success if no exception is thrown
    }

    @Test
    void createGameBad() {
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("New Game", "invalidAuthToken")));
    }

    // Positive and Negative test cases for joinGame()
    @Test
    void joinGameGood() throws Exception {
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
    void joinGameBad() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("m", "m", "m"));
        assertThrows(Exception.class, () -> facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, -1, result.authToken())));
    }

    // Positive and Negative test cases for logout()
    @Test
    void logoutGood() throws Exception {
        LogRegResult result = facade.register(new RegisterRequest("o", "o", "o"));
        var request = new LogoutRequest(result.authToken());
        facade.logout(request);
        assertTrue(true); // Success if no exception is thrown
    }

    @Test
    void logoutBad() {
        assertThrows(Exception.class, () -> facade.logout(new LogoutRequest("invalidAuthToken")));
    }
}
