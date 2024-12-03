package network;

import chess.ChessGame;
import ui.BoardPrinter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static gson.Serializer.deserialize;

public class WebsocketCommunicator extends Endpoint {
    private Session session;

    public WebsocketCommunicator() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) { // gets triggered every time a message is received

                System.out.println("\n Message received by Websocket Communicator:");
                System.out.println("printing raw message" + message);
                System.out.println(receive(message)); //  remember that I need to pass the server facade as "this"
            }
        });
    }

    public String receive(String message) {
        // Deserialize object and return the relevant message
        var parsedObject = deserialize(message, ServerMessage.class);
        ServerMessage.ServerMessageType type = parsedObject.getServerMessageType();
        return switch (type) {
            case LOAD_GAME:
                var object3 = deserialize(message, LoadGameMessage.class);
                BoardPrinter printer = new BoardPrinter();
                printer.print(ChessGame.TeamColor.WHITE, object3.getGame().getBoard().getBoard());
            case ERROR:
                var object2 = deserialize(message, ErrorMessage.class);
                yield "ERROR:" + object2.getMessage();
            case ServerMessage.ServerMessageType.NOTIFICATION:
                var object = deserialize(message, NotificationMessage.class);
                yield "NOTIFICATION: " + object.getMessage();
        };
    }

    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}
    public void onOpen(Session session, EndpointConfig endpointConfig) {}


    // Helper method to read InputStream and return it as a String
    private String readInputStream(InputStream stream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString().trim();
    }
}
