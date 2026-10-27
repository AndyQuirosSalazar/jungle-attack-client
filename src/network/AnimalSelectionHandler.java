package network;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.AnimalSpecies;
import model.GameModel;

public class AnimalSelectionHandler implements IMessageHandler {
    private GameModel model;

    public AnimalSelectionHandler(GameModel model) {
        this.model = model;
    }

    /** model.setAvailableSpecies(available, opponentChoice) */
    @Override
    public void handle(Message message) {
        List<AnimalSpecies> available = new ArrayList<>();
        JsonObject payload = message.getPayload();
        JsonElement array = payload == null ? null : payload.get("available");
        if (array != null && array.isJsonArray()) {
            for (JsonElement element : (JsonArray) array) {
                AnimalSpecies species = element.isJsonNull() ? null : AnimalSpecies.fromName(element.getAsString());
                if (species != null) {
                    available.add(species);
                }
            }
        }
        // opponentChoice no viene (o viene null) mientras el rival no ha elegido
        AnimalSpecies opponentChoice = AnimalSpecies.fromName(PayloadFields.getString(message, "opponentChoice"));
        model.setAvailableSpecies(available, opponentChoice);
    }
}
