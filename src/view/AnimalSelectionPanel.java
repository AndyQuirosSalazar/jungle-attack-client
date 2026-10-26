package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import constants.IConstants;
import controller.LobbyController;
import model.AnimalSpecies;

public class AnimalSelectionPanel extends JPanel {
    private static final int ICON_SIZE = 96;
    private static final Color MY_CHOICE_COLOR = IConstants.SELECTION_COLOR;
    private static final Color RIVAL_CHOICE_COLOR = IConstants.OPPONENT_COLOR;

    private List<JButton> speciesButtons; // un botón con imagen por especie (mismo orden que AnimalSpecies.values())
    private JLabel lblStatus;             // qué eligió el rival
    private JLabel lblError;              // errores del servidor (por ejemplo, especie ya tomada)
    private Border defaultBorder;         // borde normal de un botón, para quitar las marcas
    private AnimalSpecies myChoice;       // la especie que este jugador pidió (null si aún no)

    /** Al pulsar una especie se la pide al LobbyController. */
    public AnimalSelectionPanel(LobbyController controller) {
        setLayout(new BorderLayout(0, 16));
        setPreferredSize(new Dimension(IConstants.WINDOW_WIDTH, IConstants.WINDOW_HEIGHT));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Elige tu animal", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 32f));
        add(title, BorderLayout.NORTH);

        speciesButtons = new ArrayList<>();
        JPanel grid = new JPanel(new GridLayout(2, 4, 16, 16));
        for (AnimalSpecies species : AnimalSpecies.values()) {
            JButton button = new JButton(species.getDisplayName(),
                    new ImageIcon(SvgCache.getInstance().get(species.getSvgFile(), ICON_SIZE)));
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setFocusable(false);
            button.addActionListener(e -> {
                myChoice = species;
                clearError();
                refreshMarks(null);
                controller.selectAnimal(species);
            });
            if (defaultBorder == null) {
                defaultBorder = button.getBorder();
            }
            speciesButtons.add(button);
            grid.add(button);
        }
        add(grid, BorderLayout.CENTER);

        lblStatus = new JLabel("Esperando la elección del rival...", SwingConstants.CENTER);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.PLAIN, 16f));
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(new Color(200, 30, 30));
        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(lblStatus);
        south.add(lblError);
        add(south, BorderLayout.SOUTH);
    }

    /** Deshabilita la especie ya tomada. */
    public void updateAvailable(List<AnimalSpecies> available, AnimalSpecies opponentChoice) {
        AnimalSpecies[] all = AnimalSpecies.values();
        for (int i = 0; i < all.length; i++) {
            speciesButtons.get(i).setEnabled(available.contains(all[i]));
        }
        clearError();
        lblStatus.setText(opponentChoice == null
                ? "Esperando la elección del rival..."
                : "Tu rival eligió: " + opponentChoice.getDisplayName());
        refreshMarks(opponentChoice);
    }

    /** Deja el panel como nuevo (nueva partida). */
    public void reset() {
        myChoice = null;
        for (JButton button : speciesButtons) {
            button.setEnabled(true);
        }
        lblStatus.setText("Esperando la elección del rival...");
        clearError();
        refreshMarks(null);
    }

    /** Muestra un error del servidor. */
    public void showError(String message) {
        lblError.setText(message == null || message.isBlank() ? " " : message);
    }

    public void clearError() {
        lblError.setText(" ");
    }

    /** Borde amarillo en mi especie y rojo en la del rival. */
    private void refreshMarks(AnimalSpecies opponentChoice) {
        AnimalSpecies[] all = AnimalSpecies.values();
        for (int i = 0; i < all.length; i++) {
            JButton button = speciesButtons.get(i);
            if (all[i] == myChoice) {
                button.setBorder(BorderFactory.createLineBorder(MY_CHOICE_COLOR, 4));
            } else if (all[i] == opponentChoice) {
                button.setBorder(BorderFactory.createLineBorder(RIVAL_CHOICE_COLOR, 4));
            } else {
                button.setBorder(defaultBorder);
            }
        }
    }
}
