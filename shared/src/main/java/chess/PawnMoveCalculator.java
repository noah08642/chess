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
            }
            else {
                normalPawnMove(moves, color);
            }
        }

        else if (color == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7) {
                if (position.getRow() == 2) {
                    normalPawnMove(moves, color);
                    doubleMove(moves, color);
                }
                else {
                    normalPawnMove(moves, color);
                }
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
            ChessMove newMove = new ChessMove(position, newPosition, null);
            moves.add(createMove(position, newPosition));
            }
        ChessPosition newPosition2 = new ChessPosition(row + direction, column + 1);
        if (inbounds(newPosition2)) {
            if (isEnemy(position, newPosition2, board)) {
                ChessMove newMove = new ChessMove(position, newPosition2, null);
                moves.add(createMove(position, newPosition2));
            }
        }
        ChessPosition newPosition3 = new ChessPosition(row + direction, column - 1);
        if (inbounds(newPosition3)) {
            if (isEnemy(position, newPosition3, board)) {
                ChessMove newMove = new ChessMove(position, newPosition3, null);
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
        if (vacant(row + direction, column, board)) {
            ChessMove newMove = new ChessMove(position, new ChessPosition(row + 2, column), null);
        }
    }

}
