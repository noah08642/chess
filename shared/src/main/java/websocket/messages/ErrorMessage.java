package websocket.messages;

import chess.ChessGame;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    String getMessage() {
        return errorMessage;
    }
}
