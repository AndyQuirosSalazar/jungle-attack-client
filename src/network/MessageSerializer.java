package network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MessageSerializer {
    private static final Gson GSON = new Gson(); // instancia única de Gson

    private MessageSerializer() { }

    /** Objeto -> una línea JSON. */
    public static String toJson(Message message) {
        // Gson no usa pretty printing por defecto, así que el resultado es una sola línea
        return GSON.toJson(message);
    }

    /** Una línea JSON -> objeto. */
    public static Message fromJson(String line) {
        if (line == null || line.isBlank()) {
            throw new JsonParseException("Línea vacía");
        }
        Message message = GSON.fromJson(line, Message.class);
        if (message == null || message.getType() == null) {
            throw new JsonParseException("Mensaje sin tipo válido: " + line);
        }
        return message;
    }

    /** Convierte el payload a un record de model.dto.Payloads. */
    public static <T> T payloadAs(Message m, Class<T> type) {
        JsonObject payload = m.getPayload() != null ? m.getPayload() : new JsonObject();
        return GSON.fromJson(payload, type);
    }

    /** Convierte cualquier valor a JsonElement; lo usa Message.create para no tener otra instancia de Gson. */
    static JsonElement toTree(Object value) {
        return GSON.toJsonTree(value);
    }
}
