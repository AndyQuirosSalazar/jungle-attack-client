package view;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.GameModel;

public class HudPanel extends JPanel {
    private JLabel lblBotsKilled;        // bots eliminados
    private JLabel lblCups;              // tazas consumidas
    private JLabel lblDamage;            // capacidad de daño del pack
    private List<JButton> zoneButtons;   // uno por zona; habilitado solo si model.isZoneVisible(id)

    public void refresh(GameModel model) {
        // TODO
    }
}
