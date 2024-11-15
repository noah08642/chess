package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String notificationMessage;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.notificationMessage = notificationMessage;
    }

    String getMessage() {
        return notificationMessage;
    }
}
