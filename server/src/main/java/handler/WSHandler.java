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
            connectionManager.sendUser(new ErrorMessage( e.getMessage()), session);
        }
    }

    private void handleConnect(ConnectCommand command, Session session) throws DataAccessException, IOException {
        if (connectionManager==null) {connectionManager = new ConnectionManager();}
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {
            ErrorMessage error = new ErrorMessage("Invalid auth");
            connectionManager.sendUser(error, session);
            return;
        }
        if (connectionManager == null) { connectionManager = new ConnectionManager(); }
        connectionManager.add(gameID, session);

        GameData gameData = getGame(command.getGameID());
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
        connectionManager.sendUser(loadGameMessage, session);

        String message;
        String user = getUserFromAuth(command.getAuthToken());
        if (blah(gameID, user)== ChessGame.TeamColor.BLACK) {message = user + " has joined the game as Black";}
        else if (blah(gameID, user)==ChessGame.TeamColor.WHITE) {message = user + " has joined the game as White";}
        else{message = user + " has joined as an observer";}
        NotificationMessage notificationMessage = new NotificationMessage(message);
        connectionManager.broadcast(gameID, notificationMessage, session);
    }

    private void handleLeave(LeaveCommand command, Session session) throws IOException, DataAccessException {
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        if (!isValidAuth(auth)) {connectionManager.sendUser(new ErrorMessage("ERROR: auth is not valid"), session);}

        String user = getUserFromAuth(auth);
        NotificationMessage message = new NotificationMessage(user + " is leaving the game");
        try {connectionManager.broadcast(gameID, message, session);}
        catch (Exception ex){connectionManager.sendUser(new ErrorMessage("ERROR: " + ex.getMessage()), session);}

        try {
            if (gameDAO == null) {
                gameDAO = new SQLGameDAO();
            }
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame.TeamColor colorToReplace;
            if (Objects.equals(gameData.whiteUsername(), getUserFromAuth(auth))) {
                colorToReplace = ChessGame.TeamColor.WHITE;
                gameDAO.addPlayer(colorToReplace, gameID, null);

            }
            else if (Objects.equals(gameData.blackUsername(), getUserFromAuth(auth))){
                colorToReplace = ChessGame.TeamColor.BLACK;
                gameDAO.addPlayer(colorToReplace, gameID, null);
            }

        } catch (DataAccessException ex) {connectionManager.sendUser(new ErrorMessage("ERROR: " + ex.getMessage()), session);}
        connectionManager.remove(command.getGameID(), session);
    }

    private void handleResign(ResignCommand command, Session session) throws IOException, DataAccessException {
        if (!isValidAuth(command.getAuthToken())) {
            try {connectionManager.sendUser(new ErrorMessage("ERROR: auth is not valid"), session);}
            catch(IOException ex) {System.err.println("Unable to send message");}
        }

        GameData gameData;
        try {gameData = getGame(command.getGameID());
        } catch (DataAccessException ex) {
            connectionManager.sendUser(new ErrorMessage("Unable to get game"), session);
            return;
        }

        if (gameData.isOver()) {
            connectionManager.sendUser(new ErrorMessage("Game is over!"), session);
            return;
        }

        if (isObserver(command.getAuthToken(), gameData)) {
            connectionManager.sendUser(new ErrorMessage("You are not a player :/  probably cause you have no friends."), session);
            return;
        }


        try {
            String user = getUserFromAuth(command.getAuthToken());
            NotificationMessage message = new NotificationMessage("User " + user + " has resigned.");
            connectionManager.broadcast(command.getGameID(), message, session);
            connectionManager.sendUser(message, session);
        } catch(Exception ex) {connectionManager.sendUser(new ErrorMessage("ERROR: auth is not valid"), session);}


        gameDAO.setGameOver(gameData);
    }

    private boolean isObserver(String authToken, GameData gameData) throws DataAccessException {
        String username = getUserFromAuth(authToken);
        return !gameData.blackUsername().equals(username) && !gameData.whiteUsername().equals(username);
    }

    private void handleMakeMove(MakeMoveCommand command, Session session) throws IOException, DataAccessException {
        GameData gameData;
        try {gameData = getGame(command.getGameID());
        } catch (DataAccessException ex) {
            connectionManager.sendUser(new ErrorMessage("Unable to get game"), session);
            return;
        }
        if (!connectionManager.isInGame(gameData.gameID(), session)) {
            connectionManager.sendUser(new ErrorMessage("You've left the game"), session);
            return;
        }

        if (gameData.isOver()) {
            connectionManager.sendUser(new ErrorMessage("Game is over"), session);
            return;
        }

        if (!isValidAuth(command.getAuthToken())) {
            connectionManager.sendUser(new ErrorMessage("auth is not valid"), session);
            return;
        }

        //
        if (isObserver(command.getAuthToken(), gameData)) {
            connectionManager.sendUser(new ErrorMessage("You are not a player :/  probably cause you have no friends."), session);
            return;
        }

        String username = getUserFromAuth(command.getAuthToken());
        if(!gameData.blackUsername().equals(username) && !gameData.whiteUsername().equals(username)) {
            connectionManager.sendUser(new ErrorMessage("You are not a player :/  probably cause you have no friends."), session);
            return;
        }
        ChessGame.TeamColor teamColor = gameData.blackUsername().equals(username) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if(gameData.getGame().getTeamTurn() != teamColor) {
            connectionManager.sendUser(new ErrorMessage("Not your turn!"), session);
            return;
        }


        try {
            if (!isValidMove(command.getMove(), command.getGameID())) {
                connectionManager.sendUser(new ErrorMessage("move is not valid"), session);
                return;
            }


            gameData.getGame().makeMove(command.getMove());
            if (gameDAO == null) {gameDAO = new SQLGameDAO();}
            gameDAO.replaceGame(gameData);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
            connectionManager.broadcast(command.getGameID(), loadGameMessage, session);
            connectionManager.sendUser(loadGameMessage, session);

            NotificationMessage notificationMessage = new NotificationMessage(username + "made the move: " +
                    command.getMove().toString() + "  (enter 3 to select \"Make Move\")");
            connectionManager.broadcast(command.getGameID(), notificationMessage, session);

            // add in looking for check or stalemate and sending a different notification.

            if (gameData.getGame().isInStalemate(gameData.getGame().getTeamTurn())) {
                NotificationMessage inCheckMessage = new NotificationMessage(getOtherUser(gameData, username) +
                        " is in stalemate!");
                connectionManager.broadcast(command.getGameID(), inCheckMessage, session);
                gameData.setOver();
                gameDAO.replaceGame(gameData);
            }
            else if (gameData.getGame().isInCheckmate(gameData.getGame().getTeamTurn())) {
                NotificationMessage inCheckMessage = new NotificationMessage(getOtherUser(gameData, username) +
                        " is in Checkmate! \n Press 0 to return to menu");
                connectionManager.broadcast(command.getGameID(), inCheckMessage, session);
                connectionManager.sendUser(new NotificationMessage("Congratulations!  You've won! \nPress 0 to return to menu"), session);
                gameData.setOver();
                gameDAO.replaceGame(gameData);
            }
            else if (gameData.getGame().isInCheck(gameData.getGame().getTeamTurn())) {
                NotificationMessage inCheckMessage = new NotificationMessage(getOtherUser(gameData, username) + " is in check!");
                connectionManager.broadcast(command.getGameID(), inCheckMessage, session);
            }


        } catch(Exception ex) {connectionManager.sendUser(new ErrorMessage(ex.getMessage()), session);}
    }

    private String getOtherUser(GameData gameData, String username) {
        return (gameData.whiteUsername().equals(username)) ? gameData.whiteUsername() : gameData.blackUsername();
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

    private ChessGame.TeamColor blah(int gameID, String user) throws DataAccessException {
        if (gameDAO == null) {gameDAO  = new SQLGameDAO();}
        GameData game = gameDAO.getGame(gameID);
        if(Objects.equals(game.blackUsername(), user)) {return ChessGame.TeamColor.BLACK;}
        else if (Objects.equals(game.whiteUsername(), user)) {return ChessGame.TeamColor.WHITE;}
        else {return null;}
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
