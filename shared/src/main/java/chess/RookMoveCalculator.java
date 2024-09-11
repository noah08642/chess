package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements PieceMovesCalculator {

    private static ChessPosition position;
    private static ChessBoard board;

    RookMoveCalculator( ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }



    @Override
    public Collection<ChessMove> calculateMoves(){
        return new ArrayList<>();
    }

    public Collection<ChessPosition> findDestinations() {
        Collection<ChessPosition> destinations = new ArrayList<>();

        ChessPosition currentPosition = position;
        while(!isOutofBounds(currentPosition)) {
            //currentPosition
        }
        return destinations;
    }

}
