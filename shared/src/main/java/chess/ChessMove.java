package chess;

public record ChessMove (ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){}