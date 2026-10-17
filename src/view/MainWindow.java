package view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.GamePhase;
import model.IGameObserver;
import model.ModelChange;

public class MainWindow extends JFrame implements IGameObserver {
    private CardLayout layout;                  // alterna entre pantallas
    private LobbyPanel lobbyPanel;
    private AnimalSelectionPanel selectionPanel;
    private JPanel gameScreen;                  // tarjeta PLAYING: HudPanel (NORTH) y GamePanel (CENTER)
    private GamePanel gamePanel;
    private HudPanel hudPanel;
    private SummaryPanel summaryPanel;

    /** WINDOW_WIDTH x WINDOW_HEIGHT, título, cierre (cierra la conexión). */
    private void configureWindow() {
        // TODO
    }

    /** Muestra la pantalla según la fase. */
    private void showCard(GamePhase phase) {
        // TODO
    }

    /** PHASE_CHANGED -> showCard; SPECIES_UPDATED -> selectionPanel; ERROR -> lblError; STATE_UPDATED -> hudPanel.refresh. */
    @Override
    public void onModelChanged(ModelChange change) {
        // TODO
    }
}
