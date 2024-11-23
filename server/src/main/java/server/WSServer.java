package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;

import static handler.WSHandler.parseMessage;

@WebSocket
public class WSServer {


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {  // when the server gets a message from client
        System.out.printf("Received: %s", message);

        String returnString = parseMessage(session, message);

        System.out.println("about to send notification to communicator");
        session.getRemote().sendString(returnString);
    }
}
