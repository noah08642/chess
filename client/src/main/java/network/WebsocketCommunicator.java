package network;

import websocket.messages.ErrorMessage;
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
                receive(message);

                System.out.println("Message received by Websocket Communicator");
                System.out.println(message); // later you can move this to client... remember that I need to pass the server facade as "this"
            }
        });
    }

    public void receive(String message) {
        // Deserialize the JSON message into your custom object
        var parsedObject = deserialize(message, ServerMessage.class);
        ServerMessage.ServerMessageType type = parsedObject.getServerMessageType();

        switch (type) {
            case ServerMessage.ServerMessageType.ERROR:

            //error
            //load game
            //notifi ation
        }

        if (type == ServerMessage.ServerMessageType.ERROR) {
            var fullyParsed = deserialize(message, ErrorMessage.class);
            // do stuff with parsed error
        }
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
