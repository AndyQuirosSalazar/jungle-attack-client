package network;

import java.util.Map;

import model.GameModel;

/** Strategy: tipo -> manejador. */
public class MessageDispatcher {
    private Map<MessageType, IMessageHandler> handlers;
    private GameModel model;

    public void register(MessageType type, IMessageHandler handler) {
        // TODO
    }

    /** Busca el manejador y lo ejecuta con SwingUtilities.invokeLater. */
    public void dispatch(Message message) {
        // TODO
    }
}
