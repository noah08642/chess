package gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import websocket.commands.UserGameCommand;

public class Serializer {

    public static <T> T deserialize(String json, Class<T> clazz) {
        var deserializer = new Gson();
        return deserializer.fromJson(json, clazz);
    }

    public static String serialize(Object object) {
        var serializer = new Gson();
        return serializer.toJson(object);
    }
}
