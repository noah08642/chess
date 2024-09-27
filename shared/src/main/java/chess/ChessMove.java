package chess;

import java.util.Objects;

public final class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return startPosition;
    }

    public ChessPosition getEndPosition() {
        return endPosition;
    }

    public ChessPiece.PieceType promotionPiece() {
        return promotionPiece;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChessMove) obj;
        return Objects.equals(this.startPosition, that.startPosition) &&
                Objects.equals(this.endPosition, that.endPosition) &&
                Objects.equals(this.promotionPiece, that.promotionPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public String toString() {
        if (promotionPiece == null) {
            return startPosition + " to " + endPosition;
        }
        else {
            return startPosition + " " + endPosition + " " + promotionPiece;
        }
    }
}