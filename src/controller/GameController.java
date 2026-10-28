package controller;

import java.util.List;
import java.util.Map;

import constants.IConstants;
import model.Animal;
import model.Entity;
import model.GameModel;
import model.Zone;
import network.Message;
import network.MessageType;
import network.ServerConnection;

public class GameController {
    private GameModel model;
    private ServerConnection connection;
    private SelectionManager selectionManager;

    public GameController(GameModel model, ServerConnection connection, SelectionManager selectionManager) {
        this.model = model;
        this.connection = connection;
        this.selectionManager = selectionManager;
    }

    /**
     * Si hay selección -> MOVE_ANIMALS { animalIds, zoneId, x, y }. Un clic cerca del camino (a menos de
     * MOVE_SNAP_DISTANCE) se ajusta al punto del camino más cercano en vez de ignorarse.
     */
    public void onMoveRequested(double x, double y) {
        List<Integer> selected = selectionManager.getSelectedIds();
        Zone zone = model.getCurrentZone();
        if (selected.isEmpty() || zone == null) {
            return;
        }
        double[] target = zone.snapToPath(x, y, IConstants.CLICK_TOLERANCE, IConstants.MOVE_SNAP_DISTANCE);
        if (target == null) {
            return; // el clic quedó lejos de cualquier camino de la zona
        }
        connection.send(Message.create(MessageType.MOVE_ANIMALS, Map.of(
                "animalIds", selected,
                "zoneId", zone.getId(),
                "x", target[0],
                "y", target[1])));
    }

    /** Si hay selección y el objetivo no es propio -> ATTACK { animalIds, targetId }. */
    public void onAttackRequested(int targetId) {
        List<Integer> selected = selectionManager.getSelectedIds();
        if (selected.isEmpty()) {
            return;
        }
        Animal target = findAnimalInCurrentZone(targetId);
        if (target == null || target.isOwn()) {
            return; // no se conoce el objetivo, o es un animal propio
        }
        connection.send(Message.create(MessageType.ATTACK, Map.of(
                "animalIds", selected,
                "targetId", targetId)));
    }

    /** Si model.isZoneVisible(zoneId) -> VIEW_ZONE { zoneId } y model.setCurrentZone. */
    public void onZoneChangeRequested(int zoneId) {
        if (!model.isZoneVisible(zoneId) || zoneId == model.getCurrentZoneId()) {
            return; // zona sin animales propios, o ya se está viendo
        }
        connection.send(Message.create(MessageType.VIEW_ZONE, Map.of("zoneId", zoneId)));
        model.setCurrentZone(zoneId);
    }

    private Animal findAnimalInCurrentZone(int animalId) {
        Zone zone = model.getCurrentZone();
        if (zone != null) {
            for (Entity entity : zone.getEntities()) {
                if (entity instanceof Animal animal && animal.getId() == animalId) {
                    return animal;
                }
            }
        }
        return null;
    }
}
