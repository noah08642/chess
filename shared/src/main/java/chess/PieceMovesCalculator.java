package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves();


    default boolean isTeammate(ChessGame.TeamColor pieceColor, ChessPosition position, ChessBoard board) {
        ChessGame.TeamColor destinationColor = board.getPiece(position).getTeamColor();
        return pieceColor == destinationColor;
    }


    default boolean inbounds(ChessPosition position) {
        return (position.getRow() >= 1) && (position.getRow() <= 8) && (position.getColumn() >= 1) && (position.getColumn() <= 8);
    }

    default ChessMove createMove(ChessPosition startPosition, ChessPosition endPosition) {
        return new ChessMove(startPosition, endPosition, null);
    }

    default void addIfValidMove (ChessPosition startPosition, int rowChange, int colChange, ChessBoard board, Collection<ChessMove> moves) {
        ChessPosition newPosition = new ChessPosition(startPosition.getRow() + rowChange, startPosition.getColumn() + colChange);
        if (inbounds(newPosition)){
            ChessPiece piece = board.getPiece(newPosition);
            if (piece == null || !isTeammate(board.getPiece(startPosition).getTeamColor(), newPosition, board)) {
                moves.add(createMove(startPosition, newPosition));
            }
        }
    }
}
