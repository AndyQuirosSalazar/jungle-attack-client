package network;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import model.GameModel;

/** Strategy: tipo -> manejador. */
public class MessageDispatcher {
    private Map<MessageType, IMessageHandler> handlers;
    private GameModel model;

    public MessageDispatcher(GameModel model) {
        this.handlers = new EnumMap<>(MessageType.class);
        this.model = model;
    }

    public void register(MessageType type, IMessageHandler handler) {
        if (type != null && handler != null) {
            handlers.put(type, handler);
        }
    }

    /** Busca el manejador y lo ejecuta con SwingUtilities.invokeLater. */
    public void dispatch(Message message) {
        if (message == null) {
            return;
        }
        IMessageHandler handler = handlers.get(message.getType());
        if (handler == null) {
            System.err.println("MessageDispatcher: sin manejador para " + message.getType());
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                handler.handle(message);
            } catch (RuntimeException e) {
                // un mensaje mal formado no debe tumbar la interfaz
                System.err.println("MessageDispatcher: error al procesar " + message.getType() + ": " + e);
            }
        });
    }

    /** Lo usa MessageReceiver cuando se pierde la conexión. */
    void reportError(String text) {
        SwingUtilities.invokeLater(() -> model.setError(text));
    }
}
