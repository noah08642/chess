package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import network.ServerFacade;
import request.*;
import result.LogRegResult;
import websocket.commands.ConnectCommand;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static ui.BoardPrinter.letters;
import static ui.EscapeSequences.RESET_BG_COLOR;


public class GameClient {
    ServerFacade server;
    private String authToken;
    private String username;
    private GameData game;

    public GameClient(ServerFacade server, String authToken, String username, GameData game) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
        this.game = game;
    }

    void run() throws Exception {
        server.notifyConnect(new ConnectCommand(authToken, game.gameID()));
        System.out.println(menu());
        int input = getInt();
        while(input!= 0) {
            eval(input);
            menu();
            input = getInt();
        }
    }



    public boolean eval(int input) {
        try {
            switch (input) {
                case 1 -> help();
                case 2 -> redrawBoard();
//                case 3 -> makeMove();
//                case 4 -> resign();
                case 5 -> legalMoves();
                case 0 -> {return false;}
            };
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    private void help() {
        System.out.println("Enter 0 to quit, enter 1 to see this message again, enter 2 to redraw board, enter 3 to make move, enter 4 to resign, enter 5 to see legal moves.");
    }

    private void redrawBoard() {
        ChessBoard board = new ChessBoard();
        BoardPrinter printer = new BoardPrinter();
        printer.print(ChessGame.TeamColor.WHITE, board.getBoard());
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
        ChessGame game = new ChessGame();
        Collection<ChessMove> legalMoves =  game.validMoves(position);
        boolean[][] legalSpots = new boolean[8][8];
        for (ChessMove move : legalMoves) {
            int endRow = move.getEndPosition().getRow();
            int endCol = move.getEndPosition().getColumn();
            legalSpots[endRow - 1][endCol - 1] = true;
        }

        printer.print(ChessGame.TeamColor.WHITE, game.getBoard().getBoard(), legalSpots);
        System.out.println(RESET_BG_COLOR);
        System.out.println(menu());
    }

    private int getInt() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
    private String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
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
