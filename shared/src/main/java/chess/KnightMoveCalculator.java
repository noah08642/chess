package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMovesCalculator {

    private final ChessPosition position;
    private final ChessBoard board;

    KnightMoveCalculator(ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }


    @Override
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> moves = new ArrayList<>();

        addIfValidMove(position, 1, 2, board, moves);
        addIfValidMove(position, 2, 1, board, moves);
        addIfValidMove(position, 2, -1, board, moves);
        addIfValidMove(position, 1, -2, board, moves);
        addIfValidMove(position, -1, -2, board, moves);
        addIfValidMove(position, -2, -1, board, moves);
        addIfValidMove(position, -2, 1, board, moves);
        addIfValidMove(position, -1, 2, board, moves);

        return moves;
    }

}
