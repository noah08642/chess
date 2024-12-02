package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static gson.Serializer.serialize;

public class ConnectionManager {
    public ConcurrentHashMap<Integer, Connection> connections;

    public void add(int gameID, Session session) {
        // map from gameID to array of sessions
        // if gameID already exists, get the set, add a session, put it back in.
        if(connections == null) {connections = new ConcurrentHashMap<>();}
        Connection connection = new Connection(gameID, session);
        connections.put(gameID, connection);
    }

    public void remove(int gameID) {
        connections.remove(gameID);
    }

    public void broadcast(int gameID, NotificationMessage notification, Session senderSession) throws IOException {
        if(connections == null) {connections = new ConcurrentHashMap<>();}
        senderSession.getRemote().sendString(serialize("poop made it to broadcast" + connections.toString()));
        senderSession.getRemote().sendString(serialize(connections.values()));

        if(connections.isEmpty()) {
            senderSession.getRemote().sendString(serialize("connections is empty"));
        }

        var removeList = new ArrayList<Connection>();
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.session != (senderSession) && c.gameID == gameID) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (Connection c : removeList) {
            connections.remove(c.gameID);
        }
    }
}