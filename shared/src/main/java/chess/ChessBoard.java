package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
    }

    public ChessPiece[][] getBoard() {
        return squares;
    }

    public boolean Vacant(int i, int j) {
        return squares[i][j] == null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public void addPiece(int row, int col, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ChessPiece piece = new ChessPiece(color, type);
        ChessPosition position = new ChessPosition(row, col);
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        addPiece(1, 1, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);
        addPiece(1, 2, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);
        addPiece(1, 3, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
        addPiece(1, 4, ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.WHITE);
        addPiece(1, 5, ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE);
        addPiece(1, 6, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
        addPiece(1, 7, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);
        addPiece(1, 8, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);

        addPiece(2, 1, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 2, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 3, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 4, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 5, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 6, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 7, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        addPiece(2, 8, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);


        addPiece(8, 1, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);
        addPiece(8, 2, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);
        addPiece(8, 3, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);
        addPiece(8, 4, ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.BLACK);
        addPiece(8, 5, ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK);
        addPiece(8, 6, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);
        addPiece(8, 7, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);
        addPiece(8, 8, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);

        addPiece(7, 1, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 2, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 3, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 4, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 5, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 6, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 7, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        addPiece(7, 8, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);


    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            ChessPiece[][] newSquares = new ChessPiece[8][8];
            for (int i = 0; i < 8; i++) {
                System.arraycopy(squares[i], 0, newSquares[i], 0, 8);
            }
            clone.squares = newSquares;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j] == null) {
                    sb.append(" ");
                } else {
                    sb.append(squares[i][j]);
                }
                if (j < squares[i].length - 1) {
                    sb.append(" "); // Add space between elements in the row
                }
            }
            if (i < squares[i].length + 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
