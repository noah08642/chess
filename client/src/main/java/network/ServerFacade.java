package network;

import com.google.gson.Gson;
import request.LoginRequest;
import request.RegisterRequest;
import result.LogRegResult;

import java.io.IOException;

public class ServerFacade {
    ClientCommunicator communicator;
    String url;

    public ServerFacade(String serverURL) {
        this.communicator = new ClientCommunicator();
        this.url = serverURL;
    }

    public void login(LoginRequest request) throws IOException {
        String jsonResult = communicator.doPost(url + "/session", serialize(request), null);
        LogRegResult objResult= deserialize(jsonResult, LogRegResult.class);
    }

    public void register(RegisterRequest request) throws IOException {
        String jsonResult = communicator.doPost(url + "/user", serialize(request), null);
        LogRegResult objResult= deserialize(jsonResult, LogRegResult.class);
    }

    private String serialize(Object object) {
        var serializer = new Gson();
        return serializer.toJson(object);
    }

    private <T> T deserialize(String json, Class<T> clazz) {
        var deserializer = new Gson();
        return deserializer.fromJson(json, clazz);
    }


//
//    public LogRegResult logout(LogoutRequest request) {
//
//    }
//
//    public


}
