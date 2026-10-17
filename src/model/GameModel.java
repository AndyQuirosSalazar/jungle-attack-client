package model;

import java.util.List;
import java.util.Map;

import model.dto.Payloads;

/** Subject del patrón Observer. */
public class GameModel {
    private GamePhase phase;                  // fase actual
    private String nickname;                  // apodo del jugador
    private String opponentNickname;          // apodo del rival
    private String matchCode;                 // código de la partida
    private AnimalSpecies species;            // especie propia
    private List<AnimalSpecies> availableSpecies; // especies que aún se pueden elegir
    private AnimalSpecies opponentChoice;     // especie del rival (null si no ha elegido)
    private int currentZoneId;                // zona que se está viendo
    private Map<Integer, Zone> zones; // las 12 zonas
    private List<Pack> packs;       // packs conocidos
    private List<Animal> selectedAnimals; // selección actual del jugador
    private int damageCapacity;               // daño actual del pack propio
    private int botsKilled;                   // contador de bots eliminados
    private int cupsConsumed;                 // tazas consumidas
    private List<GameEvent> lastEvents; // eventos del último snapshot
    private String lastError;                 // último mensaje de error del servidor
    private long lastSnapshotNanos;           // cuándo llegó el último STATE_UPDATE
    private MatchSummary summary;             // resumen al terminar
    private List<IGameObserver> observers; // vistas suscritas

    public void addObserver(IGameObserver o) {
        // TODO
    }

    public void removeObserver(IGameObserver o) {
        // TODO
    }

    /** Avisa a todas las vistas. */
    private void notifyObservers(ModelChange change) {
        // TODO
    }

    /** Arma las 12 zonas, guarda el rival y aplica el estado inicial; pasa a PLAYING. */
    public void applyGameStarted(Payloads.GameStarted data) {
        // TODO
    }

    /** Actualiza zona actual, zonas visibles, contadores y eventos -> STATE_UPDATED. */
    public void applySnapshot(Payloads.StateUpdate snapshot, long nowNanos) {
        // TODO
    }

    /** Guarda el MatchSummary y pasa a GAME_OVER. */
    public void applySummary(Payloads.Summary data) {
        // TODO
    }

    /** -> SPECIES_UPDATED */
    public void setAvailableSpecies(List<AnimalSpecies> available, AnimalSpecies opponentChoice) {
        // TODO
    }

    /** -> PHASE_CHANGED */
    public void setPhase(GamePhase phase) {
        // TODO
    }

    /** Solo permite zonas visibles -> ZONE_CHANGED */
    public void setCurrentZone(int zoneId) {
        // TODO
    }

    public boolean isZoneVisible(int zoneId) {
        // TODO
        return false;
    }

    public Zone getCurrentZone() {
        // TODO
        return null;
    }

    public List<Animal> getSelectedAnimals() { return selectedAnimals; }

    public List<GameEvent> getLastEvents() { return lastEvents; }

    /** min(1, (nowNanos - lastSnapshotNanos) / (TICK_MS * 1_000_000)) */
    public double getInterpolationAlpha(long nowNanos) {
        // TODO
        return 0;
    }

    /** -> ERROR */
    public void setError(String message) {
        // TODO
    }

    /** Limpia todo para volver al lobby. */
    public void reset() {
        // TODO
    }
}
