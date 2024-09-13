package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves();

    default boolean vacant(int row, int column, ChessBoard board) {
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = board.getPiece(position);
        return piece == null;
    }

    default boolean isTeammate(ChessGame.TeamColor pieceColor, ChessPosition endPosition, ChessBoard board) {
        ChessGame.TeamColor destinationColor = board.getPiece(endPosition).getTeamColor();
        return pieceColor == destinationColor;
    }

    default boolean isTeammate(ChessGame.TeamColor pieceColor, int endRow, int endCol, ChessBoard board) {
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        ChessGame.TeamColor destinationColor = board.getPiece(endPosition).getTeamColor();
        return pieceColor == destinationColor;
    }


    default boolean inbounds(ChessPosition position) {
        return (position.getRow() >= 1) && (position.getRow() <= 8) && (position.getColumn() >= 1) && (position.getColumn() <= 8);
    }

    default boolean inbounds(int row, int column) {
        return (row >= 1) && (row <= 8) && (column >= 1) && (column <= 8);
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

    default void exploreHorizontalVertical(ChessPosition position, ChessBoard board, Collection<ChessMove> moves) {
        int row = position.getRow();
        int column = position.getColumn();

        // explore up
        int increment = 1;
        moveVertical(position, board, moves, row, column, increment);

        // explore down
        increment = -1;
        moveVertical(position, board, moves, row, column, increment);

        // explore right
        increment = 1;
        moveHorizontal(position, board, moves, row, column, increment);

        // explore left
        increment = -1;
        moveHorizontal(position, board, moves, row, column, increment);


    }

    private void moveVertical(ChessPosition position, ChessBoard board, Collection<ChessMove> moves, int row, int column, int increment) {
        int direction = increment;
        while (inbounds(row + increment, column)) {
            if (vacant(row + increment, column, board)) {
                addIfValidMove(position, increment, 0, board, moves);
                increment += direction;
            }
            else {
                if (!isTeammate(board.getPiece(position).getTeamColor(), row + increment, column, board)) {
                    addIfValidMove(position, increment, 0, board, moves);
                }
                break;
            }
        }
    }

    private void moveHorizontal(ChessPosition position, ChessBoard board, Collection<ChessMove> moves, int row, int column, int increment) {
        int direction = increment;
        while (inbounds(row, column + increment)) {
            if (vacant(row, column + increment, board)) {
                addIfValidMove(position, 0, increment, board, moves);
                increment += direction;
            }
            else {
                if (!isTeammate(board.getPiece(position).getTeamColor(), row, column + increment, board)) {
                    addIfValidMove(position, 0, increment, board, moves);
                }
                break;
            }
        }
    }


    default void moveDiagonal(ChessPosition position, ChessBoard board, Collection<ChessMove> moves, int incrementVert, int incrementHor) {
        int row = position.getRow();
        int column = position.getColumn();
        int vertDirection = incrementVert;
        int horDirection = incrementHor;
        while (inbounds(row + incrementHor, column + incrementVert)) {
            if (vacant(row + incrementHor, column + incrementVert, board)) {
                addIfValidMove(position, incrementHor, incrementVert, board, moves);
                incrementHor += horDirection;
                incrementVert += vertDirection;
            }
            else {
                if (!isTeammate(board.getPiece(position).getTeamColor(), row + incrementHor, column + incrementVert, board)) {
                    addIfValidMove(position, incrementHor, incrementVert, board, moves);
                }
                break;
            }
        }
    }
}


