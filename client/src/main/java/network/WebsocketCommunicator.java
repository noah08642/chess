package network;

import chess.ChessGame;
import ui.BoardPrinter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static gson.Serializer.deserialize;

public class WebsocketCommunicator extends Endpoint {
    private Session session;
    private ServerMessageObserver observer;

    public WebsocketCommunicator(ServerMessageObserver observer) throws Exception {
        this.observer = observer;
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) { // gets triggered every time a message is received
                observer.notify(message); //  remember that I need to pass the server facade as "this"
            }
        });
    }


    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
