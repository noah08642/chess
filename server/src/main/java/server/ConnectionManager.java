package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static gson.Serializer.serialize;

public class ConnectionManager {
    public ConcurrentHashMap<Integer, ArrayList<Connection>> connections;

    public void add(int gameID, Session session) {
        if (connections == null) {
            connections = new ConcurrentHashMap<>();
        }

        // Initialize the list for the gameID if it doesn't exist
        connections.putIfAbsent(gameID, new ArrayList<>());

        // Add the new connection to the list
        Connection connection = new Connection(gameID, session);
        connections.get(gameID).add(connection);
    }

    public void remove(int gameID) {
        connections.remove(gameID);
    }

    public void broadcast(int gameID, ServerMessage notification, Session senderSession) throws IOException {
        if(connections == null) {connections = new ConcurrentHashMap<>();}

        if(connections.isEmpty()) {
            senderSession.getRemote().sendString(serialize("connections is empty"));
        }

        var removeList = new ArrayList<Connection>();

        for (Connection c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (c.session != senderSession) {
                    c.send(notification);
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


    public void sendError(ErrorMessage errorMessage, Session senderSession) throws IOException {
        senderSession.getRemote().sendString(serialize(errorMessage));
    }

}