package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zone {
    private final int id;                          // 0 a 11
    private final List<Integer> neighborIds;       // zonas conectadas por camino
    private final List<PathSegment> paths;         // caminos (vienen en GAME_STARTED)
    private boolean visible;                       // true si el jugador tiene un animal vivo ahí
    private final List<Entity> entities;           // animales, tazas, bandera

    /** Se arma una sola vez en applyGameStarted; la topología no cambia durante la partida. */
    public Zone(int id, List<Integer> neighborIds, List<PathSegment> paths) {
        this.id = id;
        this.neighborIds = neighborIds == null ? List.of() : List.copyOf(neighborIds);
        this.paths = paths == null ? List.of() : List.copyOf(paths);
        this.visible = false;
        this.entities = new ArrayList<>();
    }

    public int getId() { return id; }

    public List<Integer> getNeighborIds() { return neighborIds; }

    /** Para PathRenderer. */
    public List<PathSegment> getPaths() { return paths; }

    public boolean isNeighbor(int zoneId) { return neighborIds.contains(zoneId); }

    public boolean isVisible() { return visible; }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!visible) {
            entities.clear(); // sin animales propios no se ve nada de la zona
        }
    }

    /** Solo lectura: el modelo es quien cambia el contenido. */
    public List<Entity> getEntities() { return Collections.unmodifiableList(entities); }

    /** Reemplaza el contenido con lo que trae el STATE_UPDATE. */
    public void setEntities(List<? extends Entity> newEntities) {
        entities.clear();
        if (newEntities != null) {
            entities.addAll(newEntities);
        }
    }

    public void addEntity(Entity entity) {
        if (entity != null && !entities.contains(entity)) {
            entities.add(entity);
        }
    }

    public void removeEntity(Entity entity) { entities.remove(entity); }

    public void clearEntities() { entities.clear(); }

    /** Algún PathSegment contiene el punto. */
    public boolean isOnPath(double x, double y, double tolerance) {
        for (PathSegment path : paths) {
            if (path.contains(x, y, tolerance)) {
                return true;
            }
        }
        return false;
    }
}
