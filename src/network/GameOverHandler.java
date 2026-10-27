package network;

import model.GameModel;
import model.dto.Payloads;

public class GameOverHandler implements IMessageHandler {
    private GameModel model;

    public GameOverHandler(GameModel model) {
        this.model = model;
    }

    /** model.applySummary(payloadAs(m, Payloads.Summary.class)) */
    @Override
    public void handle(Message message) {
        model.applySummary(MessageSerializer.payloadAs(message, Payloads.Summary.class));
    }
}
