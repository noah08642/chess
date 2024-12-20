package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    private  ChessGame.TeamColor color;
    private  ChessPiece[][] board;
    private final PrintStream out;
    static final List<String> LETTERS = Arrays.asList(null, "a", "b", "c", "d", "e", "f", "g", "h", null);
    static final List<String> BLACK = Arrays.asList("k","r","p","b","n","q");
    private boolean[][] legalSpots;


    public BoardPrinter() {
        this.color = null;
        this.board = null;
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8); // Initialize 'out'
    }

    public void print(ChessGame.TeamColor color, ChessPiece[][] board) {
        print(color,board, new boolean[8][8]);
    }

    public void print(ChessGame.TeamColor color, ChessPiece[][] board, boolean[][] legalSpots) {
        this.color = color;
        this.board = board;
        this.legalSpots = legalSpots;

        out.print(ERASE_SCREEN);
        drawBoard();
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void drawBoard() {
        drawHeader();

        if(color == ChessGame.TeamColor.WHITE) {
            for (int row = 1; row < 9; ++row) {
                if (row % 2 == 1) {
                    drawWhiteRow(row);
                } else {
                    drawBlackRow(row);
                }
            }
        }

        else {
            for (int row = 8; row > 0; --row) {
                if (row % 2 == 1) {
                    drawWhiteRow(row);
                } else {
                    drawBlackRow(row);
                }
            }
        }

        drawHeader();
    }

    private void drawHeader() {
        out.print(SET_BG_COLOR_DARK_GREEN);

        if (color == ChessGame.TeamColor.WHITE) {
            for (String letter : LETTERS) {
                printEdgeSquareContents(letter);
            }
        }

        else {
            for (int i = LETTERS.size() - 1; i >= 0; i--) {
                String letter = LETTERS.get(i);
                printEdgeSquareContents(letter);
            }
        }

        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void drawWhiteRow(int row) {
        printEdge(String.valueOf(9 - row));

        if(color == ChessGame.TeamColor.WHITE) {
            for (int col = 0; col < 8; ++col) {
                drawAppropriateSquareWhiteRow(row, col);
            }
        }

        else {
            for (int col = 7; col >= 0; --col) {
                drawAppropriateSquareWhiteRow(row, col);
            }
        }

        printEdge(String.valueOf(9 - row));
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void drawAppropriateSquareWhiteRow(int row, int col) {
        ChessPiece piece = board[8 - row][col];
        boolean isValid = legalSpots[8 - row][col];
        String letter = (piece == null) ? null : piece.toString();

        if (col % 2 == 0) {
            drawWhiteSquare(letter, isValid);
        } else {
            drawBlackSquare(letter, isValid);
        }
    }

    private void drawBlackRow(int row) {
        printEdge(String.valueOf(9 - row));

        if (color == ChessGame.TeamColor.WHITE) {
            for (int col = 0; col < 8; ++col) {
                drawAppropriateSquareBlackRow(row, col);
            }
        }

        else {
            for (int col = 7; col >=0; --col) {
                drawAppropriateSquareBlackRow(row, col);
            }
        }

        printEdge(String.valueOf(9 - row));
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void drawAppropriateSquareBlackRow(int row, int col) {
        ChessPiece piece = board[8 - row][col];
        boolean isValid = legalSpots[8 - row][col];
        String letter = (piece == null) ? null : piece.toString();

        if (col % 2 == 0) {
            drawBlackSquare(letter, isValid);
        } else {
            drawWhiteSquare(letter, isValid);
        }
    }

    private void drawWhiteSquare(String letter, boolean isValid) {
        if (isValid) { out.print(SET_BG_COLOR_GREEN);}
        else { out.print(SET_BG_COLOR_WHITE);}

        printSquareContents(letter);
    }

    private void drawBlackSquare(String letter, boolean isValid) {
        if (isValid) { out.print(SET_BG_COLOR_DARK_GREEN);}
        else { out.print(SET_BG_COLOR_BLACK);}
        printSquareContents(letter);
    }

    private void printSquareContents(String letter) {
        if (letter == null) {
            out.print("   ");
        } else {
            out.print(" ");
            printPieceSquare(letter);
            out.print(" ");
        }
    }

    private void printEdgeSquareContents(String letter) {
        out.print(SET_TEXT_COLOR_WHITE);
        if (letter == null) {
            out.print("   ");
        } else {
            out.print(" ");
            out.print(letter);
            out.print(" ");
        }
    }

    private void printEdge(String letter) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        printEdgeSquareContents(letter);
    }

    private void printPieceSquare(String letter) {
        if (BLACK.contains(letter)) {
            out.print(SET_TEXT_COLOR_BLUE);
            out.print(letter.toUpperCase());
        }
        else {
            out.print(SET_TEXT_COLOR_RED);
            out.print(letter);
        }
    }
}
