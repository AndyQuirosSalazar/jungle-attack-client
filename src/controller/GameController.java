package controller;

import model.GameModel;
import network.ServerConnection;

public class GameController {
    private GameModel model;
    private ServerConnection connection;
    private SelectionManager selectionManager;

    /** Si hay selección y zone.isOnPath(x, y, CLICK_TOLERANCE) -> MOVE_ANIMALS { animalIds, zoneId, x, y }. */
    public void onMoveRequested(double x, double y) {
        // TODO
    }

    /** Si hay selección y el objetivo no es propio -> ATTACK { animalIds, targetId }. */
    public void onAttackRequested(int targetId) {
        // TODO
    }

    /** Si model.isZoneVisible(zoneId) -> VIEW_ZONE { zoneId } y model.setCurrentZone. */
    public void onZoneChangeRequested(int zoneId) {
        // TODO
    }
}
