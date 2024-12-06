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

    public void remove(int gameID, Session session) {
        ArrayList<Connection> list = connections.get(gameID);
        if (list != null) {
            // Remove the session from the current game
            list.removeIf(connection -> connection.session.equals(session));

            // If the game has no more players, clean up the game
            if (list.isEmpty()) {
                connections.remove(gameID);
            }
        }

        // Move the session to a dummy game
        add(-1, session); // -1 represents the "empty game"
    }



    public boolean isInGame(int gameID, Session session) {
        for (Connection c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (c.session.equals(session)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void broadcast(int gameID, ServerMessage notification, Session senderSession) throws IOException {
        if(connections == null) {connections = new ConcurrentHashMap<>();}

        if(connections.isEmpty()) {
            senderSession.getRemote().sendString(serialize("connections is empty"));
        }

        for (Connection c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (c.session != senderSession) {
                    c.send(notification);
                }
            }
        }
    }


    public void sendUser(ServerMessage message, Session senderSession) throws IOException {
        senderSession.getRemote().sendString(serialize(message));
    }

}