package network;

import model.GameModel;

public class GameStartedHandler implements IMessageHandler {
    private GameModel model;

    public GameStartedHandler(GameModel model) {
        this.model = model;
    }

    /** model.applyGameStarted(payloadAs(m, Payloads.GameStarted.class)) */
    @Override
    public void handle(Message message) {
        // TODO
    }
}
