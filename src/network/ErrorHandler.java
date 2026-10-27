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
        String text = PayloadFields.getString(message, "message");
        if (text == null || text.isBlank()) {
            text = PayloadFields.getString(message, "code"); // sin texto, al menos se muestra el código del error
        }
        model.setError(text == null ? "Error desconocido del servidor." : text);
    }
}
