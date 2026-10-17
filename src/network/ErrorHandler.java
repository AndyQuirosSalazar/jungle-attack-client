package network;

import model.GameModel;

public class ErrorHandler implements IMessageHandler {
    private GameModel model;

    public ErrorHandler(GameModel model) {
        this.model = model;
    }

    /** model.setError(message): código inválido, partida llena, especie ya tomada, etc. */
    @Override
    public void handle(Message message) {
        // TODO
    }
}
