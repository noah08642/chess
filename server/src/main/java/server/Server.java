package server;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.UserHandler;
import spark.*;
import com.google.gson.Gson;

public class Server {

    private static MemoryUserDAO udb;
    private static MemoryAuthDAO adb;
    private MemoryGameDAO gdb;

    public Server() {
        udb = new MemoryUserDAO();
        adb = new MemoryAuthDAO();
        gdb = new MemoryGameDAO();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", Server::handleRegister);
        Spark.post("/session", Server::handleLogin);
        Spark.delete("session", Server::handleLogout);

        Spark.delete("/db", (request, response) -> "goodbye!");



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static String handleRegister(spark.Request req, spark.Response res) throws DataAccessException {
        UserHandler userHandler = new UserHandler(req, udb, adb)   ;
        res.body(userHandler.handleRegister());
        return res.body();
    }

    public static String handleLogin(spark.Request req, spark.Response res) throws DataAccessException {
        UserHandler userHandler = new UserHandler(req, udb, adb);
        res.body(userHandler.handleLogin());
        return res.body();
    }

    public static String handleLogout(spark.Request req, spark.Response res) throws DataAccessException {
        UserHandler userHandler = new UserHandler(req, udb, adb);
        userHandler.handleLogout();
        return res.body();
    }
}
