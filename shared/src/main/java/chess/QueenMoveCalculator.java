package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements PieceMovesCalculator {

    private final ChessPosition position;
    private final ChessBoard board;

    QueenMoveCalculator(ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }


    @Override
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> moves = new ArrayList<>();

        exploreHorizontalVertical(position, board, moves);

        moveDiagonal(position, board, moves, 1,1);
        moveDiagonal(position, board, moves, -1,1);
        moveDiagonal(position, board, moves, 1,-1);
        moveDiagonal(position, board, moves, -1,-1);


        return moves;
    }
}
