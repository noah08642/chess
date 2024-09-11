package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves();


    default boolean isTeamate(ChessPiece piece, ChessPosition position, ChessBoard board) {
        ChessPiece.PieceType myColor = piece.getPieceType();
        ChessPiece.PieceType destinationColor = board.getPiece(position).getPieceType();
        return myColor == destinationColor;
    }


    default boolean isOutofBounds(ChessPosition position) {
        return (position.getRow() >= 1) && (position.getRow() <= 8) && (position.getColumn() >= 1) && (position.getColumn() <= 8);
    }



}
