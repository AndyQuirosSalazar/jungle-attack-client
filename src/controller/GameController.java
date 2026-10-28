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

    /** Si hay selección y zone.isOnPath(x, y, CLICK_TOLERANCE) -> MOVE_ANIMALS { animalIds, zoneId, x, y }. */
    public void onMoveRequested(double x, double y) {
        List<Integer> selected = selectionManager.getSelectedIds();
        Zone zone = model.getCurrentZone();
        if (selected.isEmpty() || zone == null || !zone.isOnPath(x, y, IConstants.CLICK_TOLERANCE)) {
            return; // sin selección, o el clic no cayó sobre un camino de la imagen de la zona
        }
        connection.send(Message.create(MessageType.MOVE_ANIMALS, Map.of(
                "animalIds", selected,
                "zoneId", zone.getId(),
                "x", x,
                "y", y)));
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
