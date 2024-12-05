package network;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(String message);
}
