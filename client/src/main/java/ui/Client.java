package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import chess.ChessGame;
import com.google.gson.Gson;

import static ui.EscapeSequences.*;
import chess.ChessBoard;


public class Client {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        BoardPrinter printer = new BoardPrinter(ChessGame.TeamColor.BLACK, board.getBoard());
        printer.print();
    }
    // this does all the output for menu stuff.  and it calls the BoardPrinter...

    // it should have multiple levels of menus...

    // you need to keep track of whether they're signed in or not.
    // the easiest way is to check if authToken is null.  If it is, send them the non-logged-in menu.
}
