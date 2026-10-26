package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import constants.IConstants;
import controller.LobbyController;
import model.GameModel;
import model.MatchSummary;
import model.Pack;
import model.PackType;

public class SummaryPanel extends JPanel {
    public JButton btnBackToLobby; // LobbyController.backToLobby()

    private GameModel model;       // para saber quién soy y de qué jugador es cada pack
    private JLabel lblTitle;       // "¡Ganaste!", "Perdiste" o "¡Empate!"
    private JPanel linesPanel;     // una línea por dato del resumen
    private List<JLabel> lines;

    public SummaryPanel(LobbyController controller, GameModel model) {
        this.model = model;
        this.lines = new ArrayList<>();
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(IConstants.WINDOW_WIDTH, IConstants.WINDOW_HEIGHT));

        lblTitle = new JLabel(" ", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 36f));

        linesPanel = new JPanel(new GridBagLayout());
        linesPanel.setOpaque(false);

        btnBackToLobby = new JButton("Volver al inicio");
        btnBackToLobby.addActionListener(e -> controller.backToLobby());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(10, 10, 10, 10);
        c.gridy = 0;
        add(lblTitle, c);
        c.gridy = 1;
        add(linesPanel, c);
        c.gridy = 2;
        add(btnBackToLobby, c);
    }

    /** Ganador y motivo, bots eliminados, bajas por pack, tazas por jugador y duración. */
    public void showSummary(MatchSummary summary) {
        linesPanel.removeAll();
        lines.clear();
        if (summary == null) {
            lblTitle.setText(" ");
            revalidate();
            repaint();
            return;
        }

        lblTitle.setText(titleFor(summary));
        lblTitle.setForeground(colorFor(summary));

        addLine("Motivo: " + reasonText(summary.getReason()));
        addLine("Duración de la partida: " + formatDuration(summary.getDurationMs()));
        addLine("Bots eliminados: " + summary.getBotsEliminated());
        addLine("Tazas consumidas: tú " + summary.getPlayerCups() + ", rival " + summary.getOpponentCups());
        for (String casualty : casualtyLines(summary.getAnimalsEliminatedByPack())) {
            addLine(casualty);
        }
        revalidate();
        repaint();
    }

    // ---------------------------------------------------------------- textos

    private String titleFor(MatchSummary summary) {
        String winner = summary.getWinnerNickname();
        if (winner == null || winner.isBlank()) {
            return "¡Empate!";
        }
        String me = model.getNickname();
        if (me == null) {
            return "Ganó " + winner;
        }
        return winner.equals(me) ? "¡Ganaste!" : "Perdiste. Ganó " + winner;
    }

    private Color colorFor(MatchSummary summary) {
        String winner = summary.getWinnerNickname();
        if (winner == null || winner.isBlank()) {
            return Color.DARK_GRAY;
        }
        return winner.equals(model.getNickname()) ? IConstants.PLAYER_COLOR : IConstants.OPPONENT_COLOR;
    }

    private String reasonText(String reason) {
        if (reason == null) {
            return "—";
        }
        return switch (reason) {
            case "FLAG_TOUCHED" -> "se tocó la bandera del rival";
            case "ALL_DEAD" -> "un jugador se quedó sin animales";
            case "OPPONENT_LEFT" -> "el oponente abandonó la partida";
            default -> reason;
        };
    }

    /** Tus bajas y las del rival por separado; las de todos los bots juntas. */
    private List<String> casualtyLines(Map<Integer, Integer> deathsByPack) {
        int mine = 0;
        int rival = 0;
        int bots = 0;
        boolean hasMine = false;
        boolean hasRival = false;
        boolean hasBots = false;
        for (Map.Entry<Integer, Integer> entry : deathsByPack.entrySet()) {
            PackType type = typeOfPack(entry.getKey());
            if (type == PackType.PLAYER) {
                mine += entry.getValue();
                hasMine = true;
            } else if (type == PackType.OPPONENT) {
                rival += entry.getValue();
                hasRival = true;
            } else {
                bots += entry.getValue(); // BOT, o un pack que el modelo ya no conoce
                hasBots = true;
            }
        }
        List<String> result = new ArrayList<>();
        if (hasMine) {
            result.add("Bajas de tu pack: " + mine);
        }
        if (hasRival) {
            result.add("Bajas del pack rival: " + rival);
        }
        if (hasBots) {
            result.add("Bajas entre los bots: " + bots);
        }
        return result;
    }

    private PackType typeOfPack(int packId) {
        for (Pack pack : model.getPacks()) {
            if (pack.getId() == packId) {
                return pack.getType();
            }
        }
        return PackType.BOT;
    }

    /** 125000 ms -> "2:05". */
    private static String formatDuration(long durationMs) {
        long totalSeconds = Math.max(0, durationMs) / 1000;
        return (totalSeconds / 60) + ":" + String.format("%02d", totalSeconds % 60);
    }

    private void addLine(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 18f));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = lines.size();
        c.insets = new Insets(4, 0, 4, 0);
        linesPanel.add(label, c);
        lines.add(label);
    }
}
