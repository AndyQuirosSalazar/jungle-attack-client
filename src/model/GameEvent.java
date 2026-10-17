package model;

public class GameEvent {
    private EventType type; // ATTACK, ANIMAL_DIED, CUP_COLLECTED, BOT_KILLED
    private int sourceId;   // quién genera el evento
    private int targetId;   // a quién afecta

    /** Sirve para disparar animaciones y sonidos. */
    public EventType getType() { return type; }
}
