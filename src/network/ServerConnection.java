package network;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Singleton (holder idiom). */
public class ServerConnection {

    /** Clase interna que guarda INSTANCE. */
    private static class Holder {
        private static final ServerConnection INSTANCE = new ServerConnection();
    }

    private Socket socket;
    private BufferedReader reader;   // UTF-8
    private PrintWriter writer;      // UTF-8
    private MessageReceiver receiver; // hilo lector

    private ServerConnection() {}

    public static ServerConnection getInstance() {
        return Holder.INSTANCE;
    }

    /** Abre el socket e inicia el hilo lector. */
    public void connect(MessageDispatcher dispatcher) {
        // TODO
    }

    /** Envía un mensaje (sincronizado, lo llaman varios controladores). */
    public synchronized void send(Message message) {
        // TODO
    }

    /** Cierra socket y flujos en finally. */
    public void close() {
        // TODO
    }
}
