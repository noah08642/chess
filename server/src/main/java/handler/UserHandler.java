package handler;

import com.google.gson.Gson;
import dataaccess.*;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LogRegResult;
import result.LogoutResult;
import server.UserService;

public class UserHandler {

    spark.Request req;
    UserService userService;

    public UserHandler(spark.Request req, MemoryUserDAO udb, MemoryAuthDAO adb) {
        this.req = req;
        this.userService = new UserService(udb, adb);
    }

    public String handleLogin() throws DataAccessException {
        var serializer = new Gson();
        var loginObject = serializer.fromJson(req.body(), LoginRequest.class);
        LogRegResult result = userService.login(loginObject);

        return serializer.toJson(result);
    }

    public String handleRegister() throws DataAccessException {
        var serializer = new Gson();
        var registerObject = serializer.fromJson(req.body(), RegisterRequest.class);
        LogRegResult result = userService.register(registerObject);

        return serializer.toJson(result);
    }

    public void handleLogout() throws DataAccessException {
        String authToken = req.headers("Authorization");
        LogoutRequest request = new LogoutRequest(authToken);
        userService.logout(request);
    }
}
