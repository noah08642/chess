package server;

import handler.WSHandler;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;

@WebSocket
public class WSServer {

    private WSHandler handler;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {  // when the server gets a message from client

        if (handler == null) { handler = new WSHandler(); }

        handler.handleMessage(session, message);
    }
}
