package service;

import service.UserService;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LogRegResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private SQLAuthDAO adb;


    @BeforeEach
    public void setUp() throws DataAccessException {
        SQLUserDAO udb = new SQLUserDAO();
        SQLAuthDAO adb = new SQLAuthDAO();
        this.adb = adb;
        userService = new UserService(udb, adb);
        udb.clear();
        adb.clear();
    }

    @Test
    public void registerPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Luke", "Passwordy", "luke@gmail.com");
        LogRegResult result = userService.register(request);

        assertEquals("Luke", result.username());
        assertNotNull(result.authToken());
    }


    @Test
    public void registerNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Luke", "Passwordy", "luke@gmail.com");
        userService.register(request);

        // register again
        DataAccessException exception = assertThrows(DataAccessException.class, () -> userService.register(request));
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    public void loginPositive() throws DataAccessException {
        // register a new user
        RegisterRequest request = new RegisterRequest("noah", "poop", "noah@gmail.com");
        userService.register(request);

        // log in with credentials
        LoginRequest request2 = new LoginRequest("noah", "poop");
        LogRegResult result = userService.login(request2);

        // test
        assertEquals("noah", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegative() throws DataAccessException {
        // register a new user
        RegisterRequest regRequest = new RegisterRequest("noah", "poop", "noah@gmail.com");
        userService.register(regRequest);

        // test login with incorrect password
        LoginRequest request = new LoginRequest("noah", "poopy");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> userService.login(request));
        assertEquals("Error: unauthorized", exception.getMessage());

        // test login with incorrect user
        LoginRequest request2 = new LoginRequest("noahGoo", "poop");
        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> userService.login(request2));
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void logoutPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Luke", "Passwordy", "luke@gmail.com");
        LogRegResult result = userService.register(request);
        String auth = result.authToken();

        LogoutRequest logoutRequest = new LogoutRequest(auth);
        userService.logout(logoutRequest);
    }

    @Test
    public void logoutNegative() throws DataAccessException {

        LogoutRequest logoutRequest = new LogoutRequest("badToken");
        assertThrows(DataAccessException.class, () -> userService.logout(logoutRequest));
    }
}
