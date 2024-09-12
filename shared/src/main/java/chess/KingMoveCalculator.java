package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMovesCalculator {

    private final ChessPosition position;
    private final ChessBoard board;

    KingMoveCalculator(ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }



    @Override
    public Collection<ChessMove> calculateMoves(){
        Collection <ChessMove> moves = new ArrayList<>();

        addIfValidMove(position, -1, 1, board, moves);
        addIfValidMove(position, 0, 1, board, moves);
        addIfValidMove(position, 1, 1, board, moves);
        addIfValidMove(position, -1, 0, board, moves);
        addIfValidMove(position, 1, 0, board, moves);
        addIfValidMove(position, -1, -1, board, moves);
        addIfValidMove(position, 0, -1, board, moves);
        addIfValidMove(position, 1, -1, board, moves);

        return moves;
    }
}
