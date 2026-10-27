package network;

import model.GameModel;
import model.GamePhase;

public class MatchCreatedHandler implements IMessageHandler {
    private GameModel model;

    public MatchCreatedHandler(GameModel model) {
        this.model = model;
    }

    /** Guarda el código y pasa a WAITING_OPPONENT */
    @Override
    public void handle(Message message) {
        model.setMatchCode(PayloadFields.getString(message, "code"));
        model.setPhase(GamePhase.WAITING_OPPONENT);
    }
}
