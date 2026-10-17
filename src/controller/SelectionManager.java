package controller;

import java.awt.geom.Rectangle2D;
import java.util.List;

import model.Animal;
import model.GameModel;

public class SelectionManager {
    private GameModel model;

    /** Clic simple sobre un animal propio (con Shift agrega a la selección). */
    public void selectAt(double x, double y, boolean addToSelection) {
        // TODO
    }

    /** Selección por arrastre, solo animales propios y vivos. */
    public void selectInRectangle(Rectangle2D area) {
        // TODO
    }

    public void clearSelection() {
        // TODO
    }

    /** Quita de la selección los muertos o los que ya no están en la zona actual (en cada STATE_UPDATE). */
    public void pruneSelection() {
        // TODO
    }

    public List<Integer> getSelectedIds() {
        // TODO
        return null;
    }

    /** Usa CLICK_TOLERANCE. */
    public Animal findAnimalAt(double x, double y) {
        // TODO
        return null;
    }
}
