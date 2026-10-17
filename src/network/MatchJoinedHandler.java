package network;

import model.GameModel;

public class MatchJoinedHandler implements IMessageHandler {
    private GameModel model;

    public MatchJoinedHandler(GameModel model) {
        this.model = model;
    }

    /** Guarda el apodo del rival y pasa a SELECTING_ANIMAL */
    @Override
    public void handle(Message message) {
        // TODO
    }
}
