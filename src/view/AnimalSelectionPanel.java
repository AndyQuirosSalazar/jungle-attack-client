package view;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.AnimalSpecies;

public class AnimalSelectionPanel extends JPanel {
    private List<JButton> speciesButtons; // un botón con imagen por especie

    /** Deshabilita la especie ya tomada. */
    public void updateAvailable(List<AnimalSpecies> available, AnimalSpecies opponentChoice) {
        // TODO
    }
}
