package network;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.JsonParseException;

public class MessageReceiver implements Runnable {
    private BufferedReader reader;
    private MessageDispatcher dispatcher;
    private volatile boolean running;

    public MessageReceiver(BufferedReader reader, MessageDispatcher dispatcher) {
        this.reader = reader;
        this.dispatcher = dispatcher;
        this.running = true;
    }

    /** Lee línea por línea, fromJson y delega al dispatcher; si se pierde la conexión -> model.setError(...). */
    @Override
    public void run() {
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                try {
                    dispatcher.dispatch(MessageSerializer.fromJson(line));
                } catch (JsonParseException | IllegalArgumentException e) {
                    System.err.println("MessageReceiver: mensaje inválido ignorado: " + line);
                }
            }
        } catch (IOException e) {
            // el socket se cerró o se cayó la conexión
        }
        if (running) { // si nadie pidió detenerlo, el servidor cerró la conexión
            running = false;
            dispatcher.reportError("Se perdió la conexión con el servidor.");
        }
    }

    public void stop() {
        running = false;
    }
}
