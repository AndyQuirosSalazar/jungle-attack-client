package model;

public class GameEvent {
    private EventType type; // ATTACK, ANIMAL_DIED, CUP_COLLECTED, BOT_KILLED
    private int sourceId;   // quién genera el evento
    private int targetId;   // a quién afecta

    public GameEvent(EventType type, int sourceId, int targetId) {
        this.type = type;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    /** Sirve para disparar animaciones y sonidos. */
    public EventType getType() { return type; }

    public int getSourceId() { return sourceId; }

    public int getTargetId() { return targetId; }
}
