package network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** Lectura segura de campos sueltos del payload (los que no tienen un record en Payloads). */
final class PayloadFields {

    private PayloadFields() { }

    /** El texto del campo, o null si no viene o viene null. */
    static String getString(Message message, String field) {
        JsonObject payload = message.getPayload();
        if (payload == null) {
            return null;
        }
        JsonElement value = payload.get(field);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }
}
