package network;

public interface IMessageHandler {
    /** Cada tipo de mensaje del servidor tiene su manejador. */
    void handle(Message message);
}
