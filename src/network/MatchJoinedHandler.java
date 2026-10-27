package network;

import model.GameModel;
import model.GamePhase;

public class MatchJoinedHandler implements IMessageHandler {
    private GameModel model;

    public MatchJoinedHandler(GameModel model) {
        this.model = model;
    }

    /** Guarda el apodo del rival y pasa a SELECTING_ANIMAL */
    @Override
    public void handle(Message message) {
        model.setOpponentNickname(PayloadFields.getString(message, "opponent"));
        String code = PayloadFields.getString(message, "code");
        if (code != null) {
            model.setMatchCode(code); // quien se une no lo tenía guardado
        }
        model.setPhase(GamePhase.SELECTING_ANIMAL);
    }
}
