package network;

import java.io.BufferedReader;

public class MessageReceiver implements Runnable {
    private BufferedReader reader;
    private MessageDispatcher dispatcher;
    private volatile boolean running;

    /** Lee línea por línea, fromJson y delega al dispatcher; si se pierde la conexión -> model.setError(...). */
    @Override
    public void run() {
        // TODO
    }

    public void stop() {
        // TODO
    }
}
