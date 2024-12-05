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
import websocket.messages.ErrorMessage;
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

    public void handleMessage(Session session, String message) throws IOException {
        try {
            // Deserialize the JSON message into your custom object
            var parsedObject = deserialize(message, UserGameCommand.class);
            UserGameCommand.CommandType type = parsedObject.getCommandType();

            session.getRemote().sendString(serialize("made it to parse message"));

            switch (type) {
                case UserGameCommand.CommandType.CONNECT :
                    ConnectCommand connectCommand = deserialize(message, ConnectCommand.class);
                    handleConnect(connectCommand, session);
                    return;
                case UserGameCommand.CommandType.LEAVE :
                    LeaveCommand leaveCommand = deserialize(message, LeaveCommand.class);
                    handleLeave(leaveCommand, session);
                    return;
                case UserGameCommand.CommandType.RESIGN :
                    ResignCommand resignCommand = deserialize(message, ResignCommand.class);
                    handleResign(resignCommand, session);
                    return;
                case UserGameCommand.CommandType.MAKE_MOVE:
                    MakeMoveCommand makeMoveCommand = deserialize(message, MakeMoveCommand.class);
                    handleMakeMove(makeMoveCommand, session);
                    return;
            }
        } catch (Exception e) {
            connectionManager.sendError(new ErrorMessage( e.getMessage()), session);
        }
    }

    private void handleConnect(ConnectCommand command, Session session) {
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {return;}
        if (connectionManager == null) { connectionManager = new ConnectionManager(); }
        connectionManager.add(gameID, session);
    }

    private void handleLeave(LeaveCommand command, Session session) throws IOException {
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {connectionManager.sendError(new ErrorMessage("ERROR: auth is not valid"), session);}

        NotificationMessage message = new NotificationMessage("User X is leaving the game");
        try {connectionManager.broadcast(gameID, message, session);}
        catch (Exception ex){connectionManager.sendError(new ErrorMessage("ERROR: " + ex.getMessage()), session);}

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
        } catch (DataAccessException ex) {connectionManager.sendError(new ErrorMessage("ERROR: " + ex.getMessage()), session);}
    }

    private void handleResign(ResignCommand command, Session session) throws IOException {
        if (!isValidAuth(command.getAuthToken())) {
            try {connectionManager.sendError(new ErrorMessage("ERROR: auth is not valid"), session);}
            catch(IOException ex) {System.err.println("Unable to send message");}
        }

        try {
            String user = getUserFromAuth(command.getAuthToken());
            NotificationMessage message = new NotificationMessage("User " + user + " has resigned.");
            connectionManager.broadcast(command.getGameID(), message, session);
        } catch(Exception ex) {connectionManager.sendError(new ErrorMessage("ERROR: auth is not valid"), session);}
    }

    private void handleMakeMove(MakeMoveCommand command, Session session) throws IOException {
        if (!isValidAuth(command.getAuthToken())) {
            connectionManager.sendError(new ErrorMessage("ERROR: auth is not valid"), session);        }

        // check if move is valid
        try {
            if (!isValidMove(command.getMove(), command.getGameID())) {
                connectionManager.sendError(new ErrorMessage("ERROR: move is not valid"), session);
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

        } catch(Exception ex) {connectionManager.sendError(new ErrorMessage(ex.getMessage()), session);}
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
        if (game.getGame().isEmpty(move.getStartPosition())) {return false;}
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
