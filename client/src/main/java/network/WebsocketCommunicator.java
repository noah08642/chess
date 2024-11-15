package network;

import javax.websocket.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class WebsocketCommunicator extends Endpoint {
    private Session session;

    public WebsocketCommunicator() throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
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
