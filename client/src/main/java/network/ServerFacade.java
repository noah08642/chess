package network;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import request.*;
import result.ListResult;
import result.LogRegResult;
import ui.BoardPrinter;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.List;

import static gson.Serializer.deserialize;
import static gson.Serializer.serialize;

public class ServerFacade {
    HttpCommunicator httpCommunicator;
    WebsocketCommunicator wsCommunicator;
    String url;

    public ServerFacade(String serverURL) throws Exception {
        this.httpCommunicator = new HttpCommunicator();
        this.url = serverURL;
    }

    public void passClient(ServerMessageObserver observer) throws Exception {
        this.wsCommunicator = new WebsocketCommunicator(observer);
    }

    public LogRegResult login(LoginRequest request) throws Exception {
        String jsonResult = httpCommunicator.doPost(url + "/session", serialize(request), null);
        return deserialize(jsonResult, LogRegResult.class);
    }

    public LogRegResult register(RegisterRequest request) throws Exception {
        String jsonResult = httpCommunicator.doPost(url + "/user", serialize(request), null);
        return deserialize(jsonResult, LogRegResult.class);
    }



    public List<GameData> listGames(ListRequest request) throws Exception {
        String jsonResult = httpCommunicator.doGet(url + "/game", request.authToken());
        ListResult resultObj = deserialize(jsonResult, ListResult.class);
        return resultObj.games();
    }

    public void createGame(CreateGameRequest request) throws Exception {
        httpCommunicator.doPost(url + "/game", serialize(request), request.authToken());
    }

    public void joinGame(JoinGameRequest request) throws Exception {
        httpCommunicator.doPut(url + "/game", serialize(request), request.authToken());
    }

    public void logout(LogoutRequest request) throws Exception {
        httpCommunicator.doDelete(url + "/session",  request.authToken());
    }

    public void clear(String auth) throws Exception {
        httpCommunicator.doDelete(url + "/db", auth);
    }

    public void notifyConnect(ConnectCommand command) throws Exception {
        wsCommunicator.send(serialize(command));
    }

    public void leave(LeaveCommand command) throws Exception {
        wsCommunicator.send(serialize(command));
    }

    public void makeMove(MakeMoveCommand command) throws Exception {
        wsCommunicator.send(serialize(command));
    }

    public void resign(ResignCommand command) throws Exception {
        wsCommunicator.send(serialize(command));
    }
}
