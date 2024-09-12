package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements PieceMovesCalculator {

    private final ChessPosition position;
    private final ChessBoard board;

    RookMoveCalculator( ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }


    @Override
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        int increment = 1;
        while (inbounds(row + increment, column)) {
            if (vacant(row + increment, column)) {

            }
            addIfValidMove(position, increment, 0, board, moves);
            increment ++;
            if (isTeammate(board.getPiece(position).getTeamColor(), row + increment, column, board)) {
                break;
            }
        }


        increment = -1;
        while (inbounds(row + increment, column)) {
            addIfValidMove(position, increment, 0, board, moves);
            increment --;
        }
        increment = 1;
        while (inbounds(row, column + increment)) {
            addIfValidMove(position,0, increment, board, moves);
            increment ++;
        }
        increment = -1;
        while (inbounds(row, column + increment)) {
            addIfValidMove(position,0, increment, board, moves);
            increment --;
        }

        return moves;
    }

}
