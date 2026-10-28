package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import constants.IConstants;

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

    /** Se conecta al servidor de siempre (IConstants.SERVER_HOST). */
    public void connect(MessageDispatcher dispatcher) {
        connect(dispatcher, IConstants.SERVER_HOST);
    }

    /** Abre el socket hacia host (nombre o IP del servidor; vacío = IConstants.SERVER_HOST) e inicia el hilo lector. */
    public void connect(MessageDispatcher dispatcher, String host) {
        String target = (host == null || host.isBlank()) ? IConstants.SERVER_HOST : host.trim();
        try {
            socket = new Socket(target, IConstants.SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            close(); // si falló a medias, no se deja nada abierto
            throw new UncheckedIOException(e);
        }
        receiver = new MessageReceiver(reader, dispatcher);
        Thread thread = new Thread(receiver, "message-receiver");
        thread.setDaemon(true); // no impide que la aplicación termine
        thread.start();
    }

    /** Envía un mensaje (sincronizado, lo llaman varios controladores). */
    public synchronized void send(Message message) {
        if (writer == null || message == null) {
            return; // sin conexión no hay a quién enviarle
        }
        writer.println(MessageSerializer.toJson(message));
    }

    /** Cierra socket y flujos en finally. */
    public synchronized void close() {
        if (receiver != null) {
            receiver.stop(); // primero se avisa al hilo lector, así no reporta el cierre como error
        }
        try {
            // el socket va primero: desbloquea el readLine del hilo lector (cerrar el reader antes podría trabarse)
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // ya estaba cerrado
        } finally {
            if (writer != null) {
                writer.close();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // ya estaba cerrado
            }
            writer = null;
            reader = null;
            socket = null;
        }
    }
}
