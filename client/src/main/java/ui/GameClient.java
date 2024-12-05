package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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

    void run() throws Exception {
        server.notifyConnect(new ConnectCommand(authToken, game.gameID()));
        System.out.println(menu());
        int input;
        boolean run = true;
        while(run) {
            menu();
            input = getInt();
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

    public void notify(String message) {
        var parsedObject = deserialize(message, ServerMessage.class);
        ServerMessage.ServerMessageType type = parsedObject.getServerMessageType();
        switch (type) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = deserialize(message, LoadGameMessage.class);
                BoardPrinter printer = new BoardPrinter();
                printer.print(ChessGame.TeamColor.WHITE, loadGameMessage.getGame().getBoard().getBoard());
            case ERROR:
                ErrorMessage errorMessage = deserialize(message, ErrorMessage.class);
                System.out.println("ERROR:" + errorMessage.getMessage());
            case ServerMessage.ServerMessageType.NOTIFICATION:
                NotificationMessage notificationMessage = deserialize(message, NotificationMessage.class);
                System.out.println("NOTIFICATION: " + notificationMessage.getMessage());
        }
    }

    private void makeMove() {
        updateGame();
        if (game.getGame().getTeamTurn() != teamColor) {
            System.out.println("Not your turn!");
            return;
        }
        ChessPosition firstPosition = getPositionFromUser("Enter a valid start position (c2, for example): \n");
        ChessPosition secondPosition = getPositionFromUser("Enter a valid end position (c3, for example): \n");
        if (secondPosition.getRow() == 1 || secondPosition.getRow() == 8) {
            System.out.println("Enter a promotion piece (don't actually)");
            // add in this logic
        }
        ChessMove move = new ChessMove(firstPosition, secondPosition, null);
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

    private int getInt() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
    private String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void updateGame() {
        try {this.game = server.listGames(new ListRequest(authToken)).get(this.game.gameID());}
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
