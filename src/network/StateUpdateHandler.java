package network;

import model.GameModel;

public class StateUpdateHandler implements IMessageHandler {
    private GameModel model;

    public StateUpdateHandler(GameModel model) {
        this.model = model;
    }

    /** model.applySnapshot(payloadAs(m, Payloads.StateUpdate.class), System.nanoTime()) */
    @Override
    public void handle(Message message) {
        // TODO
    }
}
