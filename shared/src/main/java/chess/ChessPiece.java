package chess;

import java.util.Collection;

import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (pieceType) {
            case KING:
                KingMoveCalculator kingCalculator = new KingMoveCalculator(board, myPosition);
                return kingCalculator.calculateMoves();
            case QUEEN:
                QueenMoveCalculator queenMoveCalculator = new QueenMoveCalculator(board, myPosition);
                return queenMoveCalculator.calculateMoves();
            case BISHOP:
                BishopMoveCalculator bishopCalculator = new BishopMoveCalculator(board, myPosition);
                return bishopCalculator.calculateMoves();
            case KNIGHT:
                KnightMoveCalculator knightMoveCalculator = new KnightMoveCalculator(board, myPosition);
                return knightMoveCalculator.calculateMoves();
            case ROOK:
                RookMoveCalculator rookCalculator = new RookMoveCalculator(board, myPosition);
                return rookCalculator.calculateMoves();
            case PAWN:
                PawnMoveCalculator pawnCalculator = new PawnMoveCalculator(board, myPosition);
                return pawnCalculator.calculateMoves();
        }
        return null;
    }
}