package model;

import static constants.IConstants.TICK_MS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

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
    private String lastError;                 // ultimo mensaje de error del servidor
    private long lastSnapshotNanos;           // cuándo llegó el ultimo STATE_UPDATE
    private MatchSummary summary;             // resumen al terminar
    private List<IGameObserver> observers; // vistas suscritas

    public GameModel() {
        this.observers = new CopyOnWriteArrayList<>(); // se puede notificar mientras alguien se suscribe
        this.zones = new TreeMap<>();
        this.packs = new ArrayList<>();
        this.selectedAnimals = new ArrayList<>();
        this.availableSpecies = new ArrayList<>();
        this.lastEvents = new ArrayList<>();
        this.phase = GamePhase.LOBBY;
    }

    public void addObserver(IGameObserver o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(IGameObserver o) {
        observers.remove(o);
    }

    /** Avisa a todas las vistas. Swing no es thread-safe: siempre se notifica en el EDT. */
    private void notifyObservers(ModelChange change) {
        Runnable notifyAll = () -> {
            for (IGameObserver o : observers) {
                o.onModelChanged(change);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            notifyAll.run();
        } else {
            SwingUtilities.invokeLater(notifyAll);
        }
    }

    /** Arma las 12 zonas, guarda el rival y aplica el estado inicial; pasa a PLAYING. */
    public void applyGameStarted(Payloads.GameStarted data) {
        if (data == null) {
            return;
        }

        zones.clear();
        packs.clear();
        selectedAnimals.clear();
        summary = null;

        if (data.zones() != null) {
            for (Payloads.ZoneInfo info : data.zones()) {
                zones.put(info.id(), new Zone(info.id(), info.neighbors(), toSegments(info.paths())));
            }
        }

        if (data.opponent() != null) {
            opponentNickname = data.opponent();
        }
        species = AnimalSpecies.fromName(data.species());
        opponentChoice = AnimalSpecies.fromName(data.opponentSpecies());

        if (data.initial() != null) {
            applySnapshot(data.initial(), System.nanoTime());
        }
        setPhase(GamePhase.PLAYING);
    }

    /** Actualiza zona actual, zonas visibles, contadores y eventos -> STATE_UPDATED. */
    public void applySnapshot(Payloads.StateUpdate snapshot, long nowNanos) {
        if (snapshot == null) {
            return;
        }

        currentZoneId = snapshot.zoneId();

        // visibilidad: una zona sin animales propios se oculta (y se vacía)
        Set<Integer> visible = snapshot.visibleZones() == null
                ? Set.of() : new HashSet<>(snapshot.visibleZones());
        for (Zone zone : zones.values()) {
            zone.setVisible(visible.contains(zone.getId()));
            if (zone.getId() != currentZoneId) {
                zone.clearEntities(); // el snapshot solo trae el contenido de la zona actual
            }
        }

        Zone current = zones.get(currentZoneId);
        if (current != null) {
            current.setEntities(buildEntities(snapshot));
        }

        damageCapacity = snapshot.damage();
        cupsConsumed = snapshot.cupsConsumed();
        botsKilled = snapshot.botsKilled();
        for (Pack pack : packs) {
            if (pack.getType() == PackType.PLAYER) {
                pack.setDamageCapacity(damageCapacity);
            }
        }

        lastEvents = toEvents(snapshot.events());
        lastSnapshotNanos = nowNanos;
        notifyObservers(ModelChange.STATE_UPDATED);
    }

    /** Guarda el MatchSummary y pasa a GAME_OVER. */
    public void applySummary(Payloads.Summary data) {
        if (data == null) {
            return;
        }
        summary = new MatchSummary(data.winner(), data.reason(), data.botsKilled(),
                data.deathsByPack(), data.playerCups(), data.opponentCups(), data.durationMs());
        selectedAnimals.clear();
        setPhase(GamePhase.GAME_OVER);
    }

    /** -> SPECIES_UPDATED */
    public void setAvailableSpecies(List<AnimalSpecies> available, AnimalSpecies opponentChoice) {
        this.availableSpecies = available == null ? new ArrayList<>() : new ArrayList<>(available);
        this.opponentChoice = opponentChoice;
        notifyObservers(ModelChange.SPECIES_UPDATED);
    }

    /** -> PHASE_CHANGED */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
        notifyObservers(ModelChange.PHASE_CHANGED);
    }

    /** Solo permite zonas visibles -> ZONE_CHANGED */
    public void setCurrentZone(int zoneId) {
        if (!isZoneVisible(zoneId) || zoneId == currentZoneId) {
            return;
        }
        Zone previous = zones.get(currentZoneId);
        if (previous != null) {
            previous.clearEntities(); // el contenido de la nueva zona llega en el próximo snapshot
        }
        currentZoneId = zoneId;
        notifyObservers(ModelChange.ZONE_CHANGED);
    }

    public boolean isZoneVisible(int zoneId) {
        Zone zone = zones.get(zoneId);
        return zone != null && zone.isVisible();
    }

    public Zone getCurrentZone() {
        return zones.get(currentZoneId);
    }

    public List<Animal> getSelectedAnimals() { return selectedAnimals; }

    /** Reemplaza la selección (lo usa SelectionManager) -> SELECTION_CHANGED */
    public void setSelectedAnimals(List<Animal> animals) {
        for (Animal animal : selectedAnimals) {
            animal.setSelected(false);
        }
        selectedAnimals = new ArrayList<>();
        if (animals != null) {
            for (Animal animal : animals) {
                if (animal.isOwn() && animal.isAlive() && !selectedAnimals.contains(animal)) {
                    animal.setSelected(true);
                    selectedAnimals.add(animal);
                }
            }
        }
        notifyObservers(ModelChange.SELECTION_CHANGED);
    }

    public List<GameEvent> getLastEvents() { return lastEvents; }

    /** min(1, (nowNanos - lastSnapshotNanos) / (TICK_MS * 1_000_000)) */
    public double getInterpolationAlpha(long nowNanos) {
        if (lastSnapshotNanos == 0) {
            return 1; // aún no hay snapshot: se dibuja la posición actual
        }
        double alpha = (nowNanos - lastSnapshotNanos) / (TICK_MS * 1_000_000.0);
        return Math.max(0, Math.min(1, alpha));
    }

    /** -> ERROR */
    public void setError(String message) {
        this.lastError = message;
        notifyObservers(ModelChange.ERROR);
    }

    /** Limpia todo para volver al lobby. */
    public void reset() {
        phase = GamePhase.LOBBY;
        nickname = null;
        opponentNickname = null;
        matchCode = null;
        species = null;
        availableSpecies = new ArrayList<>();
        opponentChoice = null;
        currentZoneId = 0;
        zones.clear();
        packs.clear();
        selectedAnimals = new ArrayList<>();
        damageCapacity = 0;
        botsKilled = 0;
        cupsConsumed = 0;
        lastEvents = new ArrayList<>();
        lastError = null;
        lastSnapshotNanos = 0;
        summary = null;
        // los observers se mantienen: las vistas siguen vivas
    }

    // ---------------------------------------------------------------- getters / setters

    public GamePhase getPhase() { return phase; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getOpponentNickname() { return opponentNickname; }

    public void setOpponentNickname(String opponentNickname) { this.opponentNickname = opponentNickname; }

    public String getMatchCode() { return matchCode; }

    public void setMatchCode(String matchCode) { this.matchCode = matchCode; }

    public AnimalSpecies getSpecies() { return species; }

    public void setSpecies(AnimalSpecies species) { this.species = species; }

    public List<AnimalSpecies> getAvailableSpecies() { return Collections.unmodifiableList(availableSpecies); }

    public AnimalSpecies getOpponentChoice() { return opponentChoice; }

    public int getCurrentZoneId() { return currentZoneId; }

    public Zone getZone(int zoneId) { return zones.get(zoneId); }

    public Map<Integer, Zone> getZones() { return Collections.unmodifiableMap(zones); }

    public List<Pack> getPacks() { return Collections.unmodifiableList(packs); }

    public int getDamageCapacity() { return damageCapacity; }

    public int getBotsKilled() { return botsKilled; }

    public int getCupsConsumed() { return cupsConsumed; }

    public String getLastError() { return lastError; }

    public MatchSummary getSummary() { return summary; }

    // ---------------------------------------------------------------- conversión de DTOs

    /** Cada double[] es {x1, y1, x2, y2}. */
    private static List<PathSegment> toSegments(List<double[]> paths) {
        List<PathSegment> segments = new ArrayList<>();
        if (paths != null) {
            for (double[] p : paths) {
                if (p != null && p.length >= 4) {
                    segments.add(new PathSegment(p[0], p[1], p[2], p[3]));
                }
            }
        }
        return segments;
    }

    /** Contenido de la zona actual: tazas, banderas y animales (en ese orden de dibujo). */
    private List<Entity> buildEntities(Payloads.StateUpdate snapshot) {
        List<Entity> entities = new ArrayList<>();

        if (snapshot.cups() != null) {
            for (Payloads.CupState cup : snapshot.cups()) {
                entities.add(new CoffeeCup(cup.id(), cup.x(), cup.y()));
            }
        }

        if (snapshot.flags() != null) {
            for (Payloads.FlagState flag : snapshot.flags()) {
                PackType owner = toPackType(flag.owner());
                if (owner != null) {
                    // FlagState no trae id: hay a lo sumo una bandera por dueño
                    entities.add(new Flag(owner.ordinal(), owner, flag.x(), flag.y()));
                }
            }
        }

        if (snapshot.animals() != null) {
            for (Payloads.AnimalState state : snapshot.animals()) {
                Animal animal = updateAnimal(state);
                if (animal != null) {
                    entities.add(animal);
                }
            }
        }
        return entities;
    }

    /** Reusa el Animal conocido (para interpolar) o lo crea y lo agrega a su pack. */
    private Animal updateAnimal(Payloads.AnimalState state) {
        AnimalSpecies animalSpecies = AnimalSpecies.fromName(state.species());
        PackType packType = toPackType(state.packType());
        if (animalSpecies == null || packType == null) {
            return null; // dato inválido del servidor
        }

        Pack pack = findOrCreatePack(state.packId(), animalSpecies, packType);
        Animal animal = pack.findMember(state.id());
        if (animal == null) {
            animal = new Animal(state.id(), animalSpecies, state.packId(), packType,
                    state.energy(), state.x(), state.y(), state.alive());
            pack.addMember(animal);
        } else {
            animal.applyState(state.energy(), state.x(), state.y(), state.alive());
        }
        return animal;
    }

    private Pack findOrCreatePack(int packId, AnimalSpecies packSpecies, PackType type) {
        for (Pack pack : packs) {
            if (pack.getId() == packId) {
                return pack;
            }
        }
        Pack pack = new Pack(packId, packSpecies, type);
        if (type == PackType.PLAYER) {
            pack.setDamageCapacity(damageCapacity);
        }
        packs.add(pack);
        return pack;
    }

    private static List<GameEvent> toEvents(List<Payloads.EventState> events) {
        List<GameEvent> result = new ArrayList<>();
        if (events != null) {
            for (Payloads.EventState e : events) {
                EventType type = toEventType(e.type());
                if (type != null) {
                    result.add(new GameEvent(type, e.sourceId(), e.targetId()));
                }
            }
        }
        return result;
    }

    private static PackType toPackType(String name) {
        if (name == null) {
            return null;
        }
        try {
            return PackType.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static EventType toEventType(String name) {
        if (name == null) {
            return null;
        }
        try {
            return EventType.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null; // evento desconocido: se ignora
        }
    }
}
