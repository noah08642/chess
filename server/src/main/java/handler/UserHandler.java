package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.LogRegResult;
import server.UserService;

public class UserHandler {

    String jsonFile;
    UserService userService;

    public UserHandler(String jsonFile, MemoryUserDAO udb, MemoryAuthDAO adb) {
        this.jsonFile = jsonFile;
        this.userService = new UserService(udb, adb);
    }

    public String handleLogin() throws DataAccessException {
        var serializer = new Gson();
        var loginObject = serializer.fromJson(jsonFile, LoginRequest.class);
        LogRegResult result = userService.login(loginObject);

        return serializer.toJson(result);
    }

    public String handleRegister() throws DataAccessException {
        var serializer = new Gson();
        var registerObject = serializer.fromJson(jsonFile, RegisterRequest.class);
        LogRegResult result = userService.register(registerObject);

        return serializer.toJson(result);
    }

    void handleLogout() throws DataAccessException {
        var serializer = new Gson();
        var logoutObject = serializer.fromJson(jsonFile, RegisterRequest.class);
        userService.register(logoutObject);
    }

    // converts http gson call



}
