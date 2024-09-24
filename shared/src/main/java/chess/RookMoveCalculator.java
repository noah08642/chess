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

        moveDirection(position, board, moves, 0,1);
        moveDirection(position, board, moves, 0,-1);
        moveDirection(position, board, moves, 1,0);
        moveDirection(position, board, moves, -1,0);



        return moves;
    }
}
