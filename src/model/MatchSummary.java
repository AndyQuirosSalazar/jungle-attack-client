package model;

import java.util.Map;

public class MatchSummary {
    private String winnerNickname; // ganador (null si hay empate)
    private String reason;         // FLAG_TOUCHED, ALL_DEAD u OPPONENT_LEFT
    private int botsEliminated;    // bots eliminados
    private Map<Integer, Integer> animalsEliminatedByPack; // bajas por pack
    private int playerCups;        // tazas consumidas por el jugador
    private int opponentCups;      // tazas consumidas por el oponente
    private long durationMs;       // duración del match

}
