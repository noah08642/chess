package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMovesCalculator {

    private final ChessPosition position;
    private final ChessBoard board;

    PawnMoveCalculator(ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }


    @Override
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2) {
                normalPawnMove(moves, color);
                doubleMove(moves, color);
            } else if (position.getRow() == 7) {
                promotionPawnMove(moves, color);
            } else {
                normalPawnMove(moves, color);
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7) {
                normalPawnMove(moves, color);
                doubleMove(moves, color);
            } else if (position.getRow() == 2) {
                promotionPawnMove(moves, color);
            } else {
                normalPawnMove(moves, color);
            }
        }
        return moves;
    }

    public void normalPawnMove(Collection<ChessMove> moves, ChessGame.TeamColor color) {
        int direction = 1;
        if (color == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        int row = position.getRow();
        int column = position.getColumn();
        ChessPosition newPosition = new ChessPosition(row + direction, column);
        if (vacant(row + direction, column, board)) {
            moves.add(createMove(position, newPosition));
        }
        ChessPosition newPosition2 = new ChessPosition(row + direction, column + 1);
        if (inbounds(newPosition2)) {
            if (isEnemy(position, newPosition2, board)) {
                moves.add(createMove(position, newPosition2));
            }
        }
        ChessPosition newPosition3 = new ChessPosition(row + direction, column - 1);
        if (inbounds(newPosition3)) {
            if (isEnemy(position, newPosition3, board)) {
                moves.add(createMove(position, newPosition3));
            }
        }

    }

    public void doubleMove(Collection<ChessMove> moves, ChessGame.TeamColor color) {
        int direction = 2;
        if (color == ChessGame.TeamColor.BLACK) {
            direction = -2;
        }

        int row = position.getRow();
        int column = position.getColumn();
        if (vacant(row + direction, column, board) && vacant(row + (direction / 2), column, board)) {
            ChessMove newMove = new ChessMove(position, new ChessPosition(row + direction, column), null);
            moves.add(newMove);
        }
    }

    public void promotionPawnMove(Collection<ChessMove> moves, ChessGame.TeamColor color) {
        int direction = 1;
        if (color == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        int row = position.getRow();
        int column = position.getColumn();
        ChessPosition newPosition = new ChessPosition(row + direction, column);
        if (vacant(row + direction, column, board)) {
            addPromotionMove(moves, position, newPosition);
        }
        ChessPosition newPosition2 = new ChessPosition(row + direction, column + 1);
        if (inbounds(newPosition2)) {
            if (isEnemy(position, newPosition2, board)) {
                addPromotionMove(moves, position, newPosition2);
            }
        }
        ChessPosition newPosition3 = new ChessPosition(row + direction, column - 1);
        if (inbounds(newPosition3)) {
            if (isEnemy(position, newPosition3, board)) {
                addPromotionMove(moves, position, newPosition3);
            }
        }
    }

    public void addPromotionMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        ChessMove newMove = new ChessMove(start, end, ChessPiece.PieceType.BISHOP);
        ChessMove newMove1 = new ChessMove(start, end, ChessPiece.PieceType.KNIGHT);
        ChessMove newMove2 = new ChessMove(start, end, ChessPiece.PieceType.ROOK);
        ChessMove newMove3 = new ChessMove(start, end, ChessPiece.PieceType.QUEEN);
        moves.add(newMove);
        moves.add(newMove1);
        moves.add(newMove2);
        moves.add(newMove3);
    }

}
