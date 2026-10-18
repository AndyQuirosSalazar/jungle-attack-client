package model.dto;

import java.util.List;
import java.util.Map;

/**
 * Copia exacta de los records del servidor
 * (ver README del servidor, sección "Package: protocol").
 * Las coordenadas siempre son locales a la zona.
 * packType es relativo a quien recibe: "PLAYER", "OPPONENT" o "BOT".
 */
public final class Payloads {

    private Payloads() {
    }

    public record AnimalState(int id, int packId, String species, String packType,
                              double x, double y, int energy, boolean alive) {
    }

    public record CupState(int id, double x, double y) {
    }

    /** owner: "PLAYER" u "OPPONENT". */
    public record FlagState(String owner, double x, double y) {
    }

    public record EventState(String type, int sourceId, int targetId) {
    }

    /** Cada elemento de paths es {x1, y1, x2, y2} → un PathSegment. */
    public record ZoneInfo(int id, List<Integer> neighbors, List<double[]> paths) {
    }

    public record StateUpdate(long serverTime, int zoneId, List<Integer> visibleZones,
                              List<AnimalState> animals, List<CupState> cups, List<FlagState> flags,
                              int damage, int cupsConsumed, int botsKilled, List<EventState> events) {
    }

    public record GameStarted(int zoneWidth, int zoneHeight, List<ZoneInfo> zones, String opponent,
                              String species, String opponentSpecies, StateUpdate initial) {
    }

    public record Summary(String winner, String reason, long durationMs, int botsKilled,
                          Map<Integer, Integer> deathsByPack, int playerCups, int opponentCups) {
    }
}
