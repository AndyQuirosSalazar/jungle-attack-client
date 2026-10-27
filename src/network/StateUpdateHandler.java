package network;

import model.GameModel;
import model.dto.Payloads;

public class StateUpdateHandler implements IMessageHandler {
    private GameModel model;

    public StateUpdateHandler(GameModel model) {
        this.model = model;
    }

    /** model.applySnapshot(payloadAs(m, Payloads.StateUpdate.class), System.nanoTime()) */
    @Override
    public void handle(Message message) {
        model.applySnapshot(MessageSerializer.payloadAs(message, Payloads.StateUpdate.class), System.nanoTime());
    }
}
