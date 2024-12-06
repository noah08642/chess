package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.message = notificationMessage;
    }

    public String getMessage() {
        return message;
    }
}


// normal move for white
// white does another turn
// white tries to move a black piece
// black try to move a white piece
// add a rook promotion
// switch end position with start position

// observer can redraw, highlight, leave
// white makes a move and observer no longer receives notification
// white and black highlight and redraw
// black checkmate


// put king in check and ask it

// resign has a confirmation
// make a move after the game is over

