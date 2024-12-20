package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import result.ErrorResult;
import spark.Spark;

public class Server {

    //private static SQLUserDAO udb;
    private static SQLUserDAO udb;
    private static SQLAuthDAO adb;
    private static SQLGameDAO gdb;

    public Server() {
        try {
            udb = new SQLUserDAO(); // This also initializes the Database
            adb = new SQLAuthDAO();
            gdb = new SQLGameDAO();

        }
        catch (DataAccessException ex){}
    }

    public static String handleRegister(spark.Request req, spark.Response res) throws DataAccessException {
        UserHandler userHandler = new UserHandler(req, udb, adb);
        res.body(userHandler.handleRegister());
        res.status(200);
        return res.body();
    }

    public static String handleLogin(spark.Request req, spark.Response res) throws DataAccessException {
        UserHandler userHandler = new UserHandler(req, udb, adb);
        res.body(userHandler.handleLogin());
        res.status(200);
        return res.body();
    }

    public static String handleLogout(spark.Request req, spark.Response res) throws DataAccessException {
        UserHandler userHandler = new UserHandler(req, udb, adb);
        userHandler.handleLogout();
        res.status(200);
        return "";
    }

    // game
    public static String handleList(spark.Request req, spark.Response res) throws DataAccessException {
        GameHandler gameHandler = new GameHandler(req, udb, adb, gdb);
        res.body(gameHandler.handleList());
        res.status(200);
        return res.body();
    }

    public static String handleCreate(spark.Request req, spark.Response res) throws DataAccessException {
        GameHandler gameHandler = new GameHandler(req, udb, adb, gdb);
        res.body(gameHandler.handleCreate());
        res.status(200);
        return res.body();
    }

    public static String handleJoin(spark.Request req, spark.Response res) throws DataAccessException {
        GameHandler gameHandler = new GameHandler(req, udb, adb, gdb);
        res.body(gameHandler.handleJoin());
        res.status(200);
        return res.body();
    }

    public static String handleClear(spark.Request req, spark.Response res) throws DataAccessException {
        ClearHandler clearHandler = new ClearHandler(udb, adb, gdb);
        res.status(200);
        return "";
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", WSServer.class);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", Server::handleRegister);
        Spark.post("/session", Server::handleLogin);
        Spark.delete("session", Server::handleLogout);
        Spark.get("/game", Server::handleList);
        Spark.post("/game", Server::handleCreate);
        Spark.put("/game", Server::handleJoin);
        Spark.delete("db", Server::handleClear);


        // Global exception handling
        Spark.exception(BadRequestException.class, (exception, req, res) -> {
            res.status(400);
            res.body(returnSerializedError(exception.getMessage()));
        });

        Spark.exception(AlreadyTakenException.class, (exception, req, res) -> {
            res.status(403);
            res.body(returnSerializedError(exception.getMessage()));
        });

        Spark.exception(InvalidAuthException.class, (exception, req, res) -> {
            res.status(401);
            res.body(returnSerializedError(exception.getMessage()));
        });

        Spark.exception(DataAccessException.class, (exception, req, res) -> {
            res.status(500);
            res.body(returnSerializedError(exception.getMessage()));
        });


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public String returnSerializedError(String message) {
        ErrorResult result = new ErrorResult(message);
        var serializer = new Gson();
        return serializer.toJson(result);
    }

    public SQLGameDAO returnGameDAO() {
        return gdb;
    }

}
