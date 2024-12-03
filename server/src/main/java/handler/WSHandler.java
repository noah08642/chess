package handler;


import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.ConnectionManager;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

import static gson.Serializer.deserialize;
import static gson.Serializer.serialize;

public class WSHandler {

    private ConnectionManager connectionManager;
    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;
    private SQLUserDAO userDAO;

    public String parseMessage(Session session, String message) throws IOException {
        try {
            // Deserialize the JSON message into your custom object
            var parsedObject = deserialize(message, UserGameCommand.class);
            UserGameCommand.CommandType type = parsedObject.getCommandType();

            session.getRemote().sendString(serialize("made it to parse message"));

            switch (type) {
                case UserGameCommand.CommandType.CONNECT :
                    ConnectCommand connectCommand = deserialize(message, ConnectCommand.class);
                    handleConnect(connectCommand, session);
                    return serialize(new NotificationMessage("Made it to CONNECT branch!  good job :)"));
                case UserGameCommand.CommandType.LEAVE :
                    LeaveCommand leaveCommand = deserialize(message, LeaveCommand.class);
                    String returnString = handleLeave(leaveCommand, session);
                    return serialize(new NotificationMessage(returnString));
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

    private String handleLeave(LeaveCommand command, Session session) throws IOException {
        try {
            session.getRemote().sendString(serialize("made it to handleLeave"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send string: " + e.getMessage());
        }

        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {return "Auth not valid";}

        NotificationMessage message = new NotificationMessage("User X is leaving the game");
        try {connectionManager.broadcast(gameID, message, session);}
        catch (Exception ex){return ex.getMessage();}

        try {
            if (gameDAO == null) {
                gameDAO = new SQLGameDAO();
            }
        } catch (DataAccessException ex) {return "not able to create gameDAO";}

        try {
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame.TeamColor colorToReplace;
            if (Objects.equals(gameData.whiteUsername(), getUserFromAuth(auth))) {
                colorToReplace = ChessGame.TeamColor.WHITE;
            }
            else {colorToReplace = ChessGame.TeamColor.BLACK;}
            gameDAO.addPlayer(colorToReplace, gameID, null);
        } catch (DataAccessException ex) {return ex.getMessage();}
        return "Everything went well";
    }


    private boolean isValidAuth(String auth) {
        // Validate the auth token
        try {
            if (authDAO == null) {authDAO  = new SQLAuthDAO();}
            if (!authDAO.authExists(auth)) {
                System.out.println("auth doesn't exist");
                return false;
            }
        } catch (Exception ex) {
            System.out.println("error in validating auth token");
        }
        return true;
    }

    private String getUserFromAuth(String auth) throws DataAccessException {
        if (authDAO == null) {authDAO  = new SQLAuthDAO();}
        return authDAO.getAuth(auth).username();
    }
}
