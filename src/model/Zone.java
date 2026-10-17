package model;

import java.util.List;

public class Zone {
    private int id;                                  // 0 a 11
    private List<Integer> neighborIds; // zonas conectadas por camino
    private List<PathSegment> paths;    // caminos (vienen en GAME_STARTED)
    private boolean visible;                         // true si el jugador tiene un animal vivo ahí
    private List<Entity> entities; // animales, tazas, bandera

    public boolean isVisible() { return visible; }

    public List<Entity> getEntities() { return entities; }

    /** Algún PathSegment contiene el punto. */
    public boolean isOnPath(double x, double y, double tolerance) {
        // TODO
        return false;
    }
}
