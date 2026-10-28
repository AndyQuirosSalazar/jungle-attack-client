package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constants.IConstants;
import model.Animal;
import model.Entity;
import model.GameModel;
import model.IGameObserver;
import model.ModelChange;
import model.Zone;
import network.Message;
import network.MessageType;
import network.ServerConnection;

/**
 * El servidor solo engancha un ATTACK si el animal ya está a ENCOUNTER_RADIUS del objetivo, así que
 * el controlador persigue: manda a los animales hacia el objetivo, los vuelve a mandar si el objetivo
 * se mueve y pide el ATTACK de cada uno en cuanto llega al alcance.
 */
public class GameController implements IGameObserver {
    private GameModel model;
    private ServerConnection connection;
    private SelectionManager selectionManager;

    private int chaseTargetId = -1;                         // -1: no hay persecución
    private final List<Integer> chasers = new ArrayList<>(); // animales que van tras el objetivo
    private final Set<Integer> engaged = new HashSet<>();    // ya pidieron ATTACK y siguen al alcance
    private final Set<Integer> moving = new HashSet<>();     // ya tienen la orden de ir hacia el objetivo
    private double lastChaseX, lastChaseY;                   // a dónde se los mandó la última vez

    public GameController(GameModel model, ServerConnection connection, SelectionManager selectionManager) {
        this.model = model;
        this.connection = connection;
        this.selectionManager = selectionManager;
        model.addObserver(this);
    }

    /** En cada STATE_UPDATE avanza la persecución; al cambiar de zona o de fase se abandona. */
    @Override
    public void onModelChanged(ModelChange change) {
        switch (change) {
            case STATE_UPDATED -> updateChase();
            case ZONE_CHANGED, PHASE_CHANGED -> stopChase();
            default -> { }
        }
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
        stopChase(); // una orden de movimiento cancela la persecución
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

    /** Si hay selección y el objetivo no es propio: los seleccionados lo persiguen y lo atacan al alcanzarlo. */
    public void onAttackRequested(int targetId) {
        List<Integer> selected = selectionManager.getSelectedIds();
        if (selected.isEmpty()) {
            return;
        }
        Animal target = findAnimalInCurrentZone(targetId);
        if (target == null || target.isOwn() || !target.isAlive()) {
            return; // no se conoce el objetivo, o es un animal propio
        }
        stopChase();
        chaseTargetId = targetId;
        chasers.addAll(selected);
        updateChase(); // los que ya están cerca atacan de una vez, los demás salen hacia el objetivo
    }

    /**
     * Los perseguidores al alcance piden ATTACK (una sola vez mientras sigan al alcance, porque cada
     * ATTACK reinicia el cooldown en el servidor); los que están lejos van hacia el objetivo.
     */
    private void updateChase() {
        if (chaseTargetId < 0) {
            return;
        }
        Animal target = findAnimalInCurrentZone(chaseTargetId);
        if (target == null || !target.isAlive()) {
            stopChase(); // murió o ya no está en la zona
            return;
        }

        double attackRange = IConstants.ENCOUNTER_RADIUS - IConstants.ATTACK_RANGE_MARGIN;
        List<Integer> toAttack = new ArrayList<>();
        List<Integer> toMove = new ArrayList<>();
        for (Iterator<Integer> it = chasers.iterator(); it.hasNext(); ) {
            int id = it.next();
            Animal chaser = findAnimalInCurrentZone(id);
            if (chaser == null || !chaser.isAlive()) {
                it.remove(); // murió o se fue de la zona
                engaged.remove(id);
                moving.remove(id);
                continue;
            }
            double distance = Math.hypot(chaser.getX() - target.getX(), chaser.getY() - target.getY());
            if (distance <= attackRange) {
                moving.remove(id);
                if (engaged.add(id)) {
                    toAttack.add(id);
                }
            } else if (engaged.contains(id) && distance <= IConstants.ENCOUNTER_RADIUS) {
                continue; // sigue peleando en el borde del alcance: no se le reinicia el ataque
            } else {
                engaged.remove(id); // se le alejó: hay que volver a perseguir
                toMove.add(id);
            }
        }
        if (chasers.isEmpty()) {
            stopChase();
            return;
        }

        if (!toAttack.isEmpty()) {
            connection.send(Message.create(MessageType.ATTACK, Map.of(
                    "animalIds", toAttack,
                    "targetId", chaseTargetId)));
        }
        if (toMove.isEmpty()) {
            return;
        }
        // se reenvía a todos si el objetivo se movió bastante; si no, solo a los que aún no tienen la orden
        boolean targetMoved = Math.hypot(target.getX() - lastChaseX, target.getY() - lastChaseY)
                > IConstants.CHASE_RESEND_DISTANCE;
        if (!targetMoved) {
            toMove.removeIf(moving::contains);
        }
        if (!toMove.isEmpty() && sendChaseMove(toMove, target)) {
            moving.addAll(toMove);
        }
    }

    /** MOVE_ANIMALS hacia el objetivo, sin acercarse al borde (el servidor lo tomaría como cruce de zona). */
    private boolean sendChaseMove(List<Integer> ids, Animal target) {
        Zone zone = model.getCurrentZone();
        if (zone == null) {
            return false;
        }
        double margin = IConstants.ZONE_EDGE_MARGIN + 1;
        double x = Math.max(margin, Math.min(IConstants.ZONE_WIDTH - margin, target.getX()));
        double y = Math.max(margin, Math.min(IConstants.ZONE_HEIGHT - margin, target.getY()));
        double[] destination = zone.snapToPath(x, y, IConstants.CLICK_TOLERANCE, IConstants.MOVE_SNAP_DISTANCE);
        if (destination == null) {
            return false;
        }
        connection.send(Message.create(MessageType.MOVE_ANIMALS, Map.of(
                "animalIds", ids,
                "zoneId", zone.getId(),
                "x", destination[0],
                "y", destination[1])));
        lastChaseX = target.getX();
        lastChaseY = target.getY();
        return true;
    }

    private void stopChase() {
        chaseTargetId = -1;
        chasers.clear();
        engaged.clear();
        moving.clear();
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
