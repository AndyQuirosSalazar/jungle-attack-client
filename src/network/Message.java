package network;

import java.util.Map;

import com.google.gson.JsonObject;

public class Message {
    private MessageType type;    // tipo del mensaje
    private JsonObject payload;  // contenido

    Message(MessageType type, JsonObject payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() { return type; }

    public JsonObject getPayload() { return payload; }

    /** Arma un mensaje a partir de un tipo y sus campos. */
    public static Message create(MessageType type, Map<String, Object> fields) {
        if (type == null) {
            throw new IllegalArgumentException("El tipo del mensaje no puede ser null");
        }
        JsonObject payload = new JsonObject();
        if (fields != null) {
            fields.forEach((key, value) -> payload.add(key, MessageSerializer.toTree(value)));
        }
        return new Message(type, payload);
    }
}
