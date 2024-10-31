package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;


    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        board.resetBoard();
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
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessBoard newBoard = board.clone();
            imagineMove(move, newBoard);
            if (!isInCheck(newBoard, color)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }


    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckHelper(teamColor, board);
    }

    public boolean isInCheck(ChessBoard newBoard, TeamColor teamColor) {
        return isInCheckHelper(teamColor, newBoard);
    }

    public boolean isInCheckHelper(TeamColor teamColor, ChessBoard newBoard) {
        ChessPosition kingPosition = findKing(teamColor, newBoard);

        if (kingPosition == null) {
            return false;  // No king found for the team, so can't be in check
        }

        // Iterate over the board using ChessBoard methods
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (kingInDanger(teamColor, newBoard, row, col, kingPosition)) {
                    return true;  // The king is under attack
                }
            }
        }

        return false;  // No moves threatening the king were found
    }

    private static boolean kingInDanger(TeamColor teamColor, ChessBoard newBoard, int row, int col, ChessPosition kingPosition) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = newBoard.getPiece(position);

        if (piece != null && piece.getTeamColor() != teamColor) {
            // Get possible moves for the opponent's piece
            Collection<ChessMove> moves = piece.pieceMoves(newBoard, position);

            // Check if any move targets the king's position
            for (ChessMove move : moves) {
                if (kingPosition.equals(move.getEndPosition())) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            // Iterate over the entire board using ChessBoard methods
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    if (validMoveRemovesCheck(teamColor, row, col)) {
                        return false;  // A valid move that removes the check
                    }
                }
            }
        } else {
            return false;  // Not in check, hence not in checkmate
        }
        return true;  // No valid moves found, team is in checkmate
    }

    private boolean validMoveRemovesCheck(TeamColor teamColor, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        if (piece != null && piece.getTeamColor() == teamColor) {
            // Get possible moves for the piece at the current position
            Collection<ChessMove> moves = piece.pieceMoves(board, position);

            // Try every move and see if it removes the check condition
            for (ChessMove move : moves) {
                ChessBoard newBoard = board.clone();
                imagineMove(move, newBoard);
                if (!isInCheck(newBoard, teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isInStalemate(TeamColor teamColor) {

        if (!isInCheck(teamColor)) {
            // Iterate over the board using ChessBoard methods
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    if (validMoveExists(teamColor, row, col)) {
                        return false;
                    }
                }
            }
        } else {
            return false;  // In check, so it's not stalemate
        }

        return true;  // No valid moves, not in check, it's a stalemate
    }

    private boolean validMoveExists(TeamColor teamColor, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        if (piece != null && piece.getTeamColor() == teamColor) {
            // Get the valid moves for the piece at the current position
            Collection<ChessMove> moves = validMoves(position);

            // If any valid move exists, it is not a stalemate
            if (!moves.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public void imagineMove(ChessMove move, ChessBoard newBoard) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = newBoard.getPiece(start);

        newBoard.addPiece(end, piece);
        newBoard.addPiece(start, null);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("Piece cannot be null: " + move);
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("invalid move: " + move);
            // could add in more logic analyzing what about the move was wrong...
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Must move your own piece: " + move);
        }


        // add the move

        if (move.promotionPiece() != null) {
            board.addPiece(end, new ChessPiece(piece.getTeamColor(), move.promotionPiece()));
        }
        else {
            board.addPiece(end, piece);
        }
        board.addPiece(start, null);
        teamTurn = (teamTurn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
    }


    public ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
        ChessPiece[][] squares = board.getBoard();
        ChessPosition kingPosition = null;

        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] != null && squares[i][j].getPieceType() == ChessPiece.PieceType.KING && squares[i][j].getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(i + 1, j + 1);
                }
            }
        }
        return kingPosition;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
