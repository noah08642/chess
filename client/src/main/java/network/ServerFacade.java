package network;

import com.google.gson.Gson;
import model.GameData;
import request.*;
import result.ListResult;
import result.LogRegResult;

import java.io.IOException;
import java.util.List;

public class ServerFacade {
    ClientCommunicator communicator;
    String url;

    public ServerFacade(String serverURL) {
        this.communicator = new ClientCommunicator();
        this.url = serverURL;
    }

    public LogRegResult login(LoginRequest request) throws IOException {
        String jsonResult = communicator.doPost(url + "/session", serialize(request), null);
        return deserialize(jsonResult, LogRegResult.class);
    }

    public LogRegResult register(RegisterRequest request) throws IOException {
        String jsonResult = communicator.doPost(url + "/user", serialize(request), null);
        return deserialize(jsonResult, LogRegResult.class);
    }

    private String serialize(Object object) {
        var serializer = new Gson();
        return serializer.toJson(object);
    }

    private <T> T deserialize(String json, Class<T> clazz) {
        var deserializer = new Gson();
        return deserializer.fromJson(json, clazz);
    }

    public List<GameData> listGames(ListRequest request) throws IOException {
        String jsonResult = communicator.doGet(url + "/game", request.authToken());
        ListResult resultObj = deserialize(jsonResult, ListResult.class);
        return resultObj.games();
    }

    public void createGame(CreateGameRequest request) throws IOException {
        communicator.doPost(url + "/game", serialize(request), request.authToken());
    }

    public void joinGame(JoinGameRequest request) throws IOException {
        communicator.doPut(url + "/game", serialize(request), request.authToken());
    }


//
//    public LogRegResult logout(LogoutRequest request) {
//
//    }
//
//    public


}
