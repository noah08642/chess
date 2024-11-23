package handler;


import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import server.ConnectionManager;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.sql.Connection;

import static gson.Serializer.deserialize;
import static gson.Serializer.serialize;

public class WSHandler {

    public static String parseMessage(Session session, String message) throws IOException {
        try {
            // Deserialize the JSON message into your custom object
            var parsedObject = deserialize(message, UserGameCommand.class);
            UserGameCommand.CommandType type = parsedObject.getCommandType();

            switch (type) {
                case UserGameCommand.CommandType.CONNECT :
                    ConnectCommand connectCommand = deserialize(message, ConnectCommand.class);
                    handleConnect(connectCommand);
                    return serialize(new NotificationMessage("Made it to CONNECT branch!  good job :)"));
                case UserGameCommand.CommandType.LEAVE :
                    LeaveCommand leaveCommand = deserialize(message, LeaveCommand.class);
                    return "found leave command";
            }

            return "Not implemented yet";

        } catch (Exception e) {
            // Handle invalid JSON or deserialization errors
            System.err.println("Error handling message: " + e.getMessage());
            return "Error: Invalid message format or data.";
        }
    }

    private static void handleConnect(ConnectCommand command, Session session) {
        String auth = command.getAuthToken();
        int gameID = command.getGameID();

        // Validate the auth token and gameID (you should implement this part)
        try { SQLAuthDAO authDAO = new SQLAuthDAO();
            if (!authDAO.authExists(auth)) {
                System.out.println("auth doesn't exist");
            }
        } catch (Exception ex) {
            System.out.println("caught exception in handle Connect");
        }

        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.add(gameID, session);
    }

}
