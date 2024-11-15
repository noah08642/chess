package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

@WebSocket
public class WSServer {
    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/ws", WSServer.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);

        parseMessage(message);

        session.getRemote().sendString("WebSocket response: " + message);
    }

    private String parseMessage(String message) {
        try {
            // Deserialize the JSON message into your custom object
            var parsedObject = MyCustomObject.fromJson(message);

            // Process the deserialized object as needed
            System.out.println("Parsed object: " + parsedObject);

            // Send a meaningful response back to the client
            String response = "Processed your data: " + parsedObject.toString();
            session.getRemote().sendString(response);

        } catch (Exception e) {
            // Handle invalid JSON or deserialization errors
            System.err.println("Error handling message: " + e.getMessage());
            session.getRemote().sendString("Error: Invalid message format or data.");
        }
    }
}
