package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import constants.IConstants;
import controller.GameController;
import model.GameModel;

public class HudPanel extends JPanel {
    private JLabel lblBotsKilled;        // bots eliminados
    private JLabel lblCups;              // tazas consumidas
    private JLabel lblDamage;            // capacidad de daño del pack
    private List<JButton> zoneButtons;   // uno por zona; habilitado solo si model.isZoneVisible(id)
    private Color defaultButtonBackground;
    private Border defaultButtonBorder;
    private Border currentZoneBorder;    // marca la zona que se está viendo

    /** Los botones de zona le piden al GameController cambiar la zona que se está viendo. */
    public HudPanel(GameController controller) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(IConstants.WINDOW_WIDTH, IConstants.HUD_HEIGHT));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        lblBotsKilled = new JLabel();
        lblCups = new JLabel();
        lblDamage = new JLabel();
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        stats.setOpaque(false);
        stats.add(lblBotsKilled);
        stats.add(lblCups);
        stats.add(lblDamage);

        zoneButtons = new ArrayList<>();
        JPanel zones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        zones.setOpaque(false);
        zones.add(new JLabel("Zonas:"));
        for (int id = 0; id < IConstants.ZONE_COUNT; id++) {
            int zoneId = id;
            JButton button = new JButton(String.valueOf(id + 1)); // se muestran del 1 al 12
            button.setMargin(new Insets(2, 6, 2, 6));
            button.setFocusable(false); // el foco se queda en el mapa
            button.setEnabled(false);
            button.addActionListener(e -> controller.onZoneChangeRequested(zoneId));
            zoneButtons.add(button);
            zones.add(button);
        }
        defaultButtonBackground = zoneButtons.get(0).getBackground();
        defaultButtonBorder = zoneButtons.get(0).getBorder();
        currentZoneBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(0, 4, 0, 4));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(stats, BorderLayout.WEST);
        content.add(zones, BorderLayout.EAST);
        add(content, BorderLayout.CENTER);

        setCounters(0, 0, 0);
    }

    public void refresh(GameModel model) {
        setCounters(model.getBotsKilled(), model.getCupsConsumed(), model.getDamageCapacity());
        for (int id = 0; id < zoneButtons.size(); id++) {
            JButton button = zoneButtons.get(id);
            boolean current = id == model.getCurrentZoneId();
            // la actual queda habilitada para que no se vea gris; el controller ignora el clic sobre ella
            button.setEnabled(current || model.isZoneVisible(id));
            button.setFont(button.getFont().deriveFont(current ? Font.BOLD : Font.PLAIN));
            button.setBackground(current ? IConstants.SELECTION_COLOR : defaultButtonBackground);
            button.setBorder(current ? currentZoneBorder : defaultButtonBorder);
        }
    }

    private void setCounters(int botsKilled, int cups, int damage) {
        lblBotsKilled.setText("Bots eliminados: " + botsKilled);
        lblCups.setText("Tazas: " + cups);
        lblDamage.setText("Daño: " + damage);
    }
}
