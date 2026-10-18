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

    public MatchSummary(String winnerNickname, String reason, int botsEliminated,
                        Map<Integer, Integer> animalsEliminatedByPack,
                        int playerCups, int opponentCups, long durationMs) {
        this.winnerNickname = winnerNickname;
        this.reason = reason;
        this.botsEliminated = botsEliminated;
        this.animalsEliminatedByPack = animalsEliminatedByPack == null
                ? Map.of() : Map.copyOf(animalsEliminatedByPack);
        this.playerCups = playerCups;
        this.opponentCups = opponentCups;
        this.durationMs = durationMs;
    }

    public String getWinnerNickname() { return winnerNickname; }

    public String getReason() { return reason; }

    public int getBotsEliminated() { return botsEliminated; }

    public Map<Integer, Integer> getAnimalsEliminatedByPack() { return animalsEliminatedByPack; }

    public int getPlayerCups() { return playerCups; }

    public int getOpponentCups() { return opponentCups; }

    public long getDurationMs() { return durationMs; }
}
