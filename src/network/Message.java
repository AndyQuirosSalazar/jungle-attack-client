package network;

import java.util.Map;

import com.google.gson.JsonObject;

public class Message {
    private MessageType type;    // tipo del mensaje
    private JsonObject payload;  // contenido

    public MessageType getType() { return type; }

    public JsonObject getPayload() { return payload; }

    /** Arma un mensaje a partir de un tipo y sus campos. */
    public static Message create(MessageType type, Map<String, Object> fields) {
        // TODO
        return null;
    }
}
