package ui;

import chess.*;
import model.GameData;
import network.ServerFacade;
import network.ServerMessageObserver;
import request.*;
import result.LogRegResult;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static gson.Serializer.deserialize;
import static ui.BoardPrinter.letters;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.InputReader.getInput;
import static ui.InputReader.getInteger;


public class GameClient implements ServerMessageObserver {
    ServerFacade server;
    private String authToken;
    private String username;
    private GameData game;
    private ChessGame.TeamColor teamColor;

    public GameClient(ServerFacade server, String authToken, String username, GameData game, ChessGame.TeamColor teamColor){
        this.server = server;
        try {this.server.passClient(this);}
        catch (Exception ex){System.out.println("Error: unable to pass Client");}
        this.authToken = authToken;
        this.username = username;
        this.game = game;
        this.teamColor = teamColor;
    }

    void run() {
        try {
            server.notifyConnect(new ConnectCommand(authToken, game.gameID()));
        } catch( Exception ex) {System.out.println("unable to connect: " + ex.getMessage());}
        System.out.println(menu());
        int input;
        boolean run = true;
        while(run) {
            input = getInteger(menu(), false);
            run = eval(input);
        }
    }



    public boolean eval(int input) {
        try {
            if (input == 0) {
                leave();
                return false;
            }
            switch  (input) {
                case 1 -> help();
                case 2 -> redrawBoard();
                case 3 -> makeMove();
                case 4 -> resign();
                case 5 -> legalMoves();
            };
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    public void observe() {
        try {server.notifyConnect(new ConnectCommand(authToken, game.gameID()));}
        catch (Exception ex) {System.out.println("Unable to connect: " + ex.getMessage());}
        int input = -1;
        while (input != 0) {input = getInteger("Press 0 when you're ready to leave\n");}
        try {server.leave(new LeaveCommand(authToken, game.gameID()) );}
        catch (Exception ex) {System.out.println("Unable to connect: " + ex.getMessage());}
    }

    public void notify(String message) {
        var parsedObject = deserialize(message, ServerMessage.class);
        ServerMessage.ServerMessageType type = parsedObject.getServerMessageType();
        switch (type) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = deserialize(message, LoadGameMessage.class);
                BoardPrinter printer = new BoardPrinter();
                printer.print(teamColor, loadGameMessage.getGame().getBoard().getBoard());
                return;
            case ERROR:
                ErrorMessage errorMessage = deserialize(message, ErrorMessage.class);
                System.out.println(RESET_BG_COLOR+ "ERROR:" + errorMessage.getMessage() + RESET_BG_COLOR);
                return;
            case ServerMessage.ServerMessageType.NOTIFICATION:
                NotificationMessage notificationMessage = deserialize(message, NotificationMessage.class);
                System.out.println(RESET_BG_COLOR +"NOTIFICATION: " + notificationMessage.getMessage() + RESET_BG_COLOR);
                return;
        }
    }

    private void makeMove() {
        updateGame();
        if (game.whiteUsername()==null || game.blackUsername()==null) {
            System.out.println(RESET_BG_COLOR+"Wait for another player to join.");
            return;
        }
        if (game.getGame().getTeamTurn() != teamColor) {
            System.out.println(RESET_BG_COLOR+"Not your turn!");
            return;
        }
        ChessPosition firstPosition = getPositionFromUser("Enter a valid start position (d2, for example):");
        ChessPosition secondPosition = getPositionFromUser("Enter a valid end position:");
        ChessMove move = new ChessMove(firstPosition, secondPosition, null);
        Collection<ChessMove> legalMoves =  game.getGame().validMoves(firstPosition);
        for(ChessMove legalMove: legalMoves) {
            if(secondPosition.equals(legalMove.getEndPosition()) && legalMove.promotionPiece() != null) {
                String prompt = "Select a promotion piece:\n" +
                        "    1 Queen\n" +
                        "    2 Rook\n" +
                        "    3 Bishop\n" +
                        "    4 Knight\n";
                int input = getInteger(prompt);
                if (input < 1 || input > 4) {
                    System.out.println("Invalid input; exiting.");
                    return;
                }
                switch (input) {
                    case 1: move.setPromotion(ChessPiece.PieceType.QUEEN);
                        break;
                    case 2: move.setPromotion(ChessPiece.PieceType.ROOK);
                        break;
                    case 3: move.setPromotion(ChessPiece.PieceType.BISHOP);
                        break;
                    case 4: move.setPromotion(ChessPiece.PieceType.KNIGHT);
                        break;
                }
                break;
            }
        }


        MakeMoveCommand command = new MakeMoveCommand(authToken, game.gameID(), move);
        try {server.makeMove(command);}
        catch (Exception ex){System.out.println("Error making move: " + ex.getMessage());}
    }

    private ChessPosition getPositionFromUser(String prompt) {
        String input = "";
        while (input.length() != 2 || input.charAt(0) < 'a' || input.charAt(0) > 'h' || input.charAt(1) < '1' || input.charAt(1) > '8') {
            System.out.println(prompt);
            input = getInput();
        }
        char first = input.charAt(0);
        char second = input.charAt(1);
        int column = first - 'a' + 1;
        int row = second - '0';
        return new ChessPosition(row, column);
    }

    private void resign() {
        try {
            server.resign(new ResignCommand(authToken, game.gameID()));
            System.out.println(RESET_BG_COLOR + "Press 0 to return to main menu");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void help() {
        System.out.println("Enter 0 to quit, enter 1 to see this message again, enter 2 to redraw board, enter 3 to make move, enter 4 to resign, enter 5 to see legal moves.");
    }

    private void redrawBoard() {
        updateGame();
        BoardPrinter printer = new BoardPrinter();
        printer.print(teamColor, game.getGame().getBoard().getBoard());
        System.out.println(RESET_BG_COLOR);
    }

    private void legalMoves() {
        BoardPrinter printer = new BoardPrinter();

        System.out.println("Enter a piece's location to see valid moves (for example: b4");
        String location = getInput();
        while(location.isEmpty() || location.length()>2 || !letters.contains(String.valueOf(location.charAt(0))) ||
            Integer.parseInt(String.valueOf(location.charAt(1))) < 1 || Integer.parseInt(String.valueOf(location.charAt(1))) > 8) {
            System.out.println("Please enter a valid location.");
            location = getInput();
        }
        String colString = String.valueOf(location.charAt(0));
        int row = Integer.parseInt(String.valueOf(location.charAt(1)));
        int col = letters.indexOf(colString);

        ChessPosition position = new ChessPosition(row, col);
        // update game:
        updateGame();


        Collection<ChessMove> legalMoves =  game.getGame().validMoves(position);
        boolean[][] legalSpots = new boolean[8][8];
        for (ChessMove move : legalMoves) {
            int endRow = move.getEndPosition().getRow();
            int endCol = move.getEndPosition().getColumn();
            legalSpots[endRow - 1][endCol - 1] = true;
        }

        printer.print(teamColor, game.getGame().getBoard().getBoard(), legalSpots);
        System.out.println(RESET_BG_COLOR);
        System.out.println(menu());
    }

    private void leave() {
        try { server.leave(new LeaveCommand(authToken, game.gameID()));}
        catch (Exception ex) {System.err.println(ex.getMessage());}
    }




    private void updateGame() {
        try {
            for (GameData game : server.listGames(new ListRequest(authToken))) {
                if(game.gameID() == this.game.gameID()) {
                    this.game = game;
                }
            }
        }
        catch (Exception ex) {System.out.println(ex.getMessage());}
    }


    public String menu() {
            return """
                    Enter a number to select:
                    
                    1 Help
                    2 Redraw Chessboard
                    3 Make Move
                    4 Resign
                    5 Highlight Legal Moves
                    0 Leave Game
                    """;
    }
}
