package network;

import com.google.gson.Gson;

public class MessageSerializer {
    private static final Gson GSON = new Gson(); // instancia única de Gson

    /** Objeto -> una línea JSON. */
    public static String toJson(Message message) {
        // TODO
        return null;
    }

    /** Una línea JSON -> objeto. */
    public static Message fromJson(String line) {
        // TODO
        return null;
    }

    /** Convierte el payload a un record de model.dto.Payloads. */
    public static <T> T payloadAs(Message m, Class<T> type) {
        // TODO
        return null;
    }
}
