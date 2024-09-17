package chess;

import java.util.Collection;

import java.util.ArrayList;
import java.util.Objects;

public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }


    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }
}