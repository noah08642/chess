package chess;

import java.util.ArrayList;
import java.util.Collection;


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

        ChessPiece[][] squares = newBoard.getBoard();
        ChessPosition kingPosition = findKing(teamColor, newBoard);
        if (kingPosition == null) {
            return false;
        }

        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] != null && squares[i][j].getTeamColor() != teamColor) {
                    ChessPosition position = new ChessPosition(i + 1, j+1);
                    ChessPiece piece = newBoard.getPiece(position);
                    Collection<ChessMove> moves = piece.pieceMoves(newBoard, position);
                    for (ChessMove move : moves) {
                        if (kingPosition.equals(move.getEndPosition())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // code for commit requirement
    // code for commit requirement
    // code for commit requirement



    public boolean isInCheckmate(TeamColor teamColor) {

        ChessPiece[][] squares = board.getBoard();

        if (isInCheck(teamColor)) {
            for (int i = 0; i < squares.length; i++) {
                for (int j = 0; j < squares[i].length; j++) {
                    if (squares[i][j] != null && teamColor == squares[i][j].getTeamColor()) {
                        ChessPosition position = new ChessPosition(i + 1, j+1);
                        ChessPiece piece = squares[i][j];
                        Collection<ChessMove> moves = piece.pieceMoves(board, position);
                        for (ChessMove move : moves) {
                            ChessBoard newBoard = board.clone();
                            imagineMove(move, newBoard);
                            if (!isInCheck(newBoard, teamColor)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        else {
            return false;
        }
        return true;
    }


    public boolean isInStalemate(TeamColor teamColor) {

        if (!isInCheck(teamColor)) {
            for (int i = 0; i < board.getBoard().length; i++) {
                for (int j = 0; j < board.getBoard()[i].length; j++) {
                    if (board.getBoard()[i][j] != null && board.getBoard()[i][j].getTeamColor() == teamColor) {
                        ChessPosition position = new ChessPosition(i + 1, j+1);
                        Collection<ChessMove> moves = validMoves(position);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        else {
            return false;
        }

        return true;
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
            throw new InvalidMoveException("Piece cannot be null: " + move.toString());
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("invalid move: " + move.toString());
            // could add in more logic analyzing what about the move was wrong...
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Must move your own piece: " + move.toString());
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
                    kingPosition = new ChessPosition(i+1, j+1);
                }
            }
        }
        return kingPosition;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
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
}
