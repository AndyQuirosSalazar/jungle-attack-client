package network;

import model.GameModel;

public class AnimalSelectionHandler implements IMessageHandler {
    private GameModel model;

    public AnimalSelectionHandler(GameModel model) {
        this.model = model;
    }

    /** model.setAvailableSpecies(available, opponentChoice) */
    @Override
    public void handle(Message message) {
        // TODO
    }
}
