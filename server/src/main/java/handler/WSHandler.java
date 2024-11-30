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

    private ConnectionManager connectionManager;

    public String parseMessage(Session session, String message) throws IOException {
        try {
            // Deserialize the JSON message into your custom object
            var parsedObject = deserialize(message, UserGameCommand.class);
            UserGameCommand.CommandType type = parsedObject.getCommandType();

            session.getRemote().sendString("made it to parse message");

            switch (type) {
                case UserGameCommand.CommandType.CONNECT :
                    ConnectCommand connectCommand = deserialize(message, ConnectCommand.class);
                    handleConnect(connectCommand, session);
                    return serialize(new NotificationMessage("Made it to CONNECT branch!  good job :)"));
                case UserGameCommand.CommandType.LEAVE :
//                    LeaveCommand leaveCommand = deserialize(message, LeaveCommand.class);
//                    handleLeave(leaveCommand, session);
                    return serialize(new NotificationMessage("made it back from leave (this is not the broadcast)"));
            }

            return "Not implemented yet";

        } catch (Exception e) {
            // Handle invalid JSON or deserialization errors
            System.err.println("Error handling message: " + e.getMessage());
            return "Error: Invalid message format or data.";
        }
    }

    private void handleConnect(ConnectCommand command, Session session) {
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {return;}


        if (connectionManager == null) { connectionManager = new ConnectionManager(); }

        connectionManager.add(gameID, session);
    }

    private void handleLeave(LeaveCommand command, Session session) {
        System.out.println("inside of handleLeave");
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {return;}

        NotificationMessage message = new NotificationMessage("User X is leaving the game");
        try {connectionManager.broadcast(gameID, message, session);}
        catch (Exception ex){System.out.println(ex.getMessage());}
    }

    private boolean isValidAuth(String auth) {
        // Validate the auth token
        try { SQLAuthDAO authDAO = new SQLAuthDAO();
            if (!authDAO.authExists(auth)) {
                System.out.println("auth doesn't exist");
                return false;
            }
        } catch (Exception ex) {
            System.out.println("error in validating auth token");
        }
        return true;
    }
}
