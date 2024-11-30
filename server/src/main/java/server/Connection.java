package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public int gameID;
    public Session session;

    public Connection(int gameID, Session session) {
        this.gameID = gameID;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        System.out.println("Inside of connection send method");
        session.getRemote().sendString(msg);
    }
}