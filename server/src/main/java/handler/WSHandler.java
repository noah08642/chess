package handler;


import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.ConnectionManager;
import service.UserService;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
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
                    return serialize(new NotificationMessage("Successfully joined game"));
                case UserGameCommand.CommandType.LEAVE :
                    LeaveCommand leaveCommand = deserialize(message, LeaveCommand.class);
                    String returnString = handleLeave(leaveCommand, session);
                    return serialize(new NotificationMessage(returnString));
                case UserGameCommand.CommandType.RESIGN :
                    ResignCommand resignCommand = deserialize(message, ResignCommand.class);
                    String resignResponse = handleResign(resignCommand, session);
                    return serialize(new NotificationMessage(resignResponse));
                case UserGameCommand.CommandType.MAKE_MOVE:
                    MakeMoveCommand makeMoveCommand = deserialize(message, MakeMoveCommand.class);
                    String makeMoveResponse = handleMakeMove(makeMoveCommand, session);
                    return serialize(new NotificationMessage(makeMoveResponse));
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

    private String handleResign(ResignCommand command, Session session) {
        if (!isValidAuth(command.getAuthToken())) {
            return "Auth not valid";
        }

        try {
            String user = getUserFromAuth(command.getAuthToken());
            NotificationMessage message = new NotificationMessage("User " + user + " has resigned.");
            connectionManager.broadcast(command.getGameID(), message, session);
        } catch(Exception ex) {return "Unable to broadcast message: " + ex.getMessage();}
        return "handleResign went well";
    }

    private String handleMakeMove(MakeMoveCommand command, Session session) {
        if (!isValidAuth(command.getAuthToken())) {
            return "Auth not valid";
        }

        // check if move is valid
        try {
            if (!isValidMove(command.getMove(), command.getGameID())) {
                return "Move is not valid";
            }

            GameData gameData = getGame(command.getGameID());
            gameData.getGame().makeMove(command.getMove());
            if (gameDAO == null) {gameDAO = new SQLGameDAO();}
            gameDAO.replaceGame(gameData);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
            connectionManager.broadcast(command.getGameID(), loadGameMessage, session);

            NotificationMessage notificationMessage = new NotificationMessage("Move was made: " + command.getMove().toString());
            connectionManager.broadcast(command.getGameID(), notificationMessage, session);

            // add in looking for check or stalemate and sending a different notification.

        } catch(Exception ex) {return ex.getMessage();}

        return "all is well in handleMakeMove";
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

    private boolean isValidMove(ChessMove move, int gameID) throws DataAccessException {
        GameData game = getGame(gameID);
        return (game.getGame().validMoves(move.getStartPosition()).contains(move)) ;
    }

    private void makeMove(ChessMove move, int gameID) throws DataAccessException, InvalidMoveException {
        GameData game = getGame(gameID);
        game.getGame().makeMove(move);
        gameDAO.replaceGame(game);
    }

    private GameData getGame(int gameID) throws DataAccessException {
        if (gameDAO ==null) {gameDAO = new SQLGameDAO();}
        return gameDAO.getGame(gameID);
    }

}
