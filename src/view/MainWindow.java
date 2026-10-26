package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import constants.IConstants;
import controller.GameController;
import controller.InputHandler;
import controller.LobbyController;
import controller.SelectionManager;
import model.GameModel;
import model.GamePhase;
import model.IGameObserver;
import model.ModelChange;
import network.ServerConnection;

public class MainWindow extends JFrame implements IGameObserver {
    private static final String CARD_LOBBY = "LOBBY";           // también sirve para WAITING_OPPONENT
    private static final String CARD_SELECTING = "SELECTING";
    private static final String CARD_PLAYING = "PLAYING";
    private static final String CARD_GAME_OVER = "GAME_OVER";

    private GameModel model;
    private SelectionManager selectionManager;
    private ServerConnection connection;

    private CardLayout layout;                  // alterna entre pantallas
    private JPanel cards;                       // el panel que usa el CardLayout
    private LobbyPanel lobbyPanel;
    private AnimalSelectionPanel selectionPanel;
    private JPanel gameScreen;                  // tarjeta PLAYING: HudPanel (NORTH) y GamePanel (CENTER)
    private GamePanel gamePanel;
    private HudPanel hudPanel;
    private SummaryPanel summaryPanel;

    /** Arma las cuatro pantallas y se suscribe al modelo. */
    public MainWindow(GameModel model, LobbyController lobbyController, GameController gameController,
                      SelectionManager selectionManager, ServerConnection connection) {
        this.model = model;
        this.selectionManager = selectionManager;
        this.connection = connection;

        lobbyPanel = new LobbyPanel(lobbyController);
        selectionPanel = new AnimalSelectionPanel(lobbyController);
        summaryPanel = new SummaryPanel(lobbyController, model);

        // el rectángulo de arrastre lo escribe el InputHandler y lo dibuja el GamePanel: tiene que ser el mismo
        SelectionBoxRenderer selectionBox = new SelectionBoxRenderer();
        InputHandler inputHandler = new InputHandler(gameController, selectionManager, selectionBox);
        hudPanel = new HudPanel(gameController);
        gamePanel = new GamePanel(model, inputHandler, selectionBox); // se suscribe al modelo antes que esta ventana
        gameScreen = new JPanel(new BorderLayout());
        gameScreen.add(hudPanel, BorderLayout.NORTH);
        gameScreen.add(gamePanel, BorderLayout.CENTER);

        layout = new CardLayout();
        cards = new JPanel(layout);
        cards.add(lobbyPanel, CARD_LOBBY);
        cards.add(selectionPanel, CARD_SELECTING);
        cards.add(gameScreen, CARD_PLAYING);
        cards.add(summaryPanel, CARD_GAME_OVER);
        add(cards);

        configureWindow();
        model.addObserver(this);
        showCard(model.getPhase());
    }

    /** WINDOW_WIDTH x WINDOW_HEIGHT, título, cierre (cierra la conexión). */
    private void configureWindow() {
        setTitle("Jungle Attack");
        setSize(IConstants.WINDOW_WIDTH, IConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // antes de salir: se detiene la animación y se cierra el socket
                gamePanel.dispose();
                connection.close();
            }
        });
    }

    /** Muestra la pantalla según la fase. */
    private void showCard(GamePhase phase) {
        switch (phase) {
            case LOBBY -> {
                lobbyPanel.reset();
                selectionPanel.reset();
                layout.show(cards, CARD_LOBBY);
            }
            case WAITING_OPPONENT -> {
                lobbyPanel.showWaiting(model.getMatchCode()); // sigue en la pantalla de inicio, con el código a la vista
                layout.show(cards, CARD_LOBBY);
            }
            case SELECTING_ANIMAL -> {
                selectionPanel.reset();
                if (!model.getAvailableSpecies().isEmpty()) {
                    selectionPanel.updateAvailable(model.getAvailableSpecies(), model.getOpponentChoice());
                }
                layout.show(cards, CARD_SELECTING);
            }
            case PLAYING -> {
                hudPanel.refresh(model);
                layout.show(cards, CARD_PLAYING);
            }
            case GAME_OVER -> {
                summaryPanel.showSummary(model.getSummary());
                layout.show(cards, CARD_GAME_OVER);
            }
        }
    }

    /** PHASE_CHANGED -> showCard; SPECIES_UPDATED -> selectionPanel; ERROR -> lblError; STATE_UPDATED -> hudPanel.refresh. */
    @Override
    public void onModelChanged(ModelChange change) {
        switch (change) {
            case PHASE_CHANGED -> showCard(model.getPhase());
            case SPECIES_UPDATED ->
                selectionPanel.updateAvailable(model.getAvailableSpecies(), model.getOpponentChoice());
            case ERROR -> showError(model.getLastError());
            case STATE_UPDATED, ZONE_CHANGED -> {
                selectionManager.pruneSelection(); // fuera los muertos y los que ya no están en esta zona
                hudPanel.refresh(model);
            }
            default -> { } // SELECTION_CHANGED: los sprites leen la selección en vivo
        }
    }

    /** El error se muestra en la pantalla donde está el jugador. */
    private void showError(String message) {
        switch (model.getPhase()) {
            case LOBBY, WAITING_OPPONENT -> lobbyPanel.showError(message);
            case SELECTING_ANIMAL -> selectionPanel.showError(message);
            default -> { } // en partida el cliente ya valida antes de enviar, así que casi no llegan errores
        }
    }
}
