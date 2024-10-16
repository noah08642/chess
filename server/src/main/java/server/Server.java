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

        Spark.delete("/db", ((request, response) -> "goodbye!"));



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
        String body = req.body();
        UserHandler userHandler = new UserHandler(body, udb, adb)   ;
        System.out.println(userHandler.handleRegister());
        res.body(userHandler.handleRegister());
        return "";
    }
}
