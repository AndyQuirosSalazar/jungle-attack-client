package app;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controller.GameController;
import controller.LobbyController;
import controller.SelectionManager;
import model.GameModel;
import network.AnimalSelectionHandler;
import network.ErrorHandler;
import network.GameOverHandler;
import network.GameStartedHandler;
import network.MatchCreatedHandler;
import network.MatchJoinedHandler;
import network.MessageDispatcher;
import network.MessageType;
import network.ServerConnection;
import network.StateUpdateHandler;
import view.MainWindow;
import view.SvgCache;

public class ClientApp {

    public static void main(String[] args) {
        // 1. Rasteriza los SVG una sola vez, antes de abrir la ventana
        SvgCache.getInstance().preload();

        // 2. Modelo y despachador; un manejador por cada mensaje que manda el servidor
        GameModel model = new GameModel();
        MessageDispatcher dispatcher = new MessageDispatcher(model);
        dispatcher.register(MessageType.MATCH_CREATED, new MatchCreatedHandler(model));
        dispatcher.register(MessageType.MATCH_JOINED, new MatchJoinedHandler(model));
        dispatcher.register(MessageType.ANIMAL_SELECTION, new AnimalSelectionHandler(model));
        dispatcher.register(MessageType.GAME_STARTED, new GameStartedHandler(model));
        dispatcher.register(MessageType.STATE_UPDATE, new StateUpdateHandler(model));
        dispatcher.register(MessageType.GAME_OVER, new GameOverHandler(model));
        dispatcher.register(MessageType.ERROR, new ErrorHandler(model));

        // 3. Conexión con el servidor; si no está encendido, se avisa y se sale
        ServerConnection connection = ServerConnection.getInstance();
        try {
            connection.connect(dispatcher);
        } catch (Exception e) {
            System.err.println("No se pudo conectar con el servidor: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con el servidor.\nRevisa que esté encendido e inténtalo de nuevo.",
                        "Jungle Attack", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
            return;
        }

        // 4. Controladores y ventana (MainWindow y GamePanel se suscriben al modelo)
        SwingUtilities.invokeLater(() -> {
            LobbyController lobbyController = new LobbyController(model, connection);
            SelectionManager selectionManager = new SelectionManager(model);
            GameController gameController = new GameController(model, connection, selectionManager);
            MainWindow window = new MainWindow(model, lobbyController, gameController, selectionManager, connection);
            window.setVisible(true);
        });
    }
}
