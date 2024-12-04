package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static gson.Serializer.serialize;

public class Connection {
    public int gameID;
    public Session session;

    public Connection(int gameID, Session session) {
        this.gameID = gameID;
        this.session = session;
    }

    public void send(ServerMessage message) throws IOException {
        session.getRemote().sendString(serialize(message));
    }


    @Override
    public String toString() {
        return "Connection{" +
                "gameID=" + gameID +
                ", session=" + session.getRemote().toString() +
                '}';
    }
}