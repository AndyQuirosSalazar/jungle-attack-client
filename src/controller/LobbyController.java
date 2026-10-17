package controller;

import model.AnimalSpecies;
import model.GameModel;
import network.ServerConnection;

public class LobbyController {
    private GameModel model;
    private ServerConnection connection;

    /** Valida el apodo y envía CREATE_MATCH; si es inválido -> model.setError. */
    public void createMatch(String nickname) {
        // TODO
    }

    /** Valida el código con MATCH_CODE_REGEX y envía JOIN_MATCH. */
    public void joinMatch(String nickname, String code) {
        // TODO
    }

    /** Envía SELECT_ANIMAL. */
    public void selectAnimal(AnimalSpecies species) {
        // TODO
    }

    /** model.reset() y setPhase(LOBBY). */
    public void backToLobby() {
        // TODO
    }

    /** Largo entre NICKNAME_MIN_LENGTH y NICKNAME_MAX_LENGTH. */
    private boolean isValidNickname(String nickname) {
        // TODO
        return false;
    }

    private boolean isValidCode(String code) {
        // TODO
        return false;
    }
}
