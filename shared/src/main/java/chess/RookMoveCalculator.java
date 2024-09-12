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

//        moves.add(returnValidMove(position, 1, 2, board));
//        moves.add(returnValidMove(position, 2, 1, board));
//        moves.add(returnValidMove(position, 2, -1, board));
//        moves.add(returnValidMove(position, 1, -2, board));
//        moves.add(returnValidMove(position, -1, -2, board));
//        moves.add(returnValidMove(position, -2, -1, board));
//        moves.add(returnValidMove(position, -2, 1, board));
//        moves.add(returnValidMove(position, -1, 2, board));

        return moves;
    }

}
