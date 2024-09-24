package chess;

import java.util.Collection;


public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;


    public ChessGame() {
        this.board = new ChessBoard();
    }

    public TeamColor getTeamTurn() {
        return teamColor;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : moves) {
            ChessBoard newBoard = new ChessBoard();
            makeMove(move)
        }









        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.startPosition();
        ChessPosition end = move.endPosition();
        ChessPiece piece = board.getPiece(start);

        board.addPiece(end, piece);
        board.addPiece(start, null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece[][] squares = board.getBoard();
        ChessPosition kingPosition = null;

        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING && squares[i][j].getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(i, j);
                }
            }
        }

        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] != null && squares[i][j].getTeamColor() != teamColor) {
                    ChessPosition position = new ChessPosition(i, j);
                    ChessPiece piece = board.getPiece(position);
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.endPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }



        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }
}
