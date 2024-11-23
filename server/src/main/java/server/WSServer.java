package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import gson.Serializer;

import java.io.IOException;
import static gson.Serializer.deserialize;
import static gson.Serializer.serialize;

@WebSocket
public class WSServer {

    //Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {  // when the server gets a message from client
        System.out.printf("Received: %s", message);

        String returnString = parseMessage(session, message);

        System.out.println("about to send notification to communicator");
        session.getRemote().sendString(returnString);
    }

    private String parseMessage(Session session, String message) throws IOException {
        try {
            // Deserialize the JSON message into your custom object
            var parsedObject = deserialize(message, UserGameCommand.class);
            UserGameCommand.CommandType type = parsedObject.getCommandType();

            switch (type) {
                case UserGameCommand.CommandType.CONNECT :
                    ConnectCommand connectCommand = deserialize(message, ConnectCommand.class);
                    // call a handler class with the connectCommand... In fact this should probably all be in a handler...
                    return serialize(new NotificationMessage("Made it to CONNECT branch!  good job :)"));
                case UserGameCommand.CommandType.LEAVE :
                    LeaveCommand leaveCommand = deserialize(message, LeaveCommand.class);
                    return "found leave command";
            }

            return "Not implemented yet";

        } catch (Exception e) {
            // Handle invalid JSON or deserialization errors
            System.err.println("Error handling message: " + e.getMessage());
            return "Error: Invalid message format or data.";
        }
    }


}
