package controller;

import java.util.Map;

import constants.IConstants;
import model.AnimalSpecies;
import model.GamePhase;
import model.GameModel;
import network.Message;
import network.MessageType;
import network.ServerConnection;

public class LobbyController {
    private GameModel model;
    private ServerConnection connection;

    public LobbyController(GameModel model, ServerConnection connection) {
        this.model = model;
        this.connection = connection;
    }

    /** Valida el apodo y envía CREATE_MATCH; si es inválido -> model.setError. */
    public void createMatch(String nickname) {
        if (!isValidNickname(nickname)) {
            model.setError(nicknameError());
            return;
        }
        String clean = nickname.trim();
        model.setNickname(clean);
        connection.send(Message.create(MessageType.CREATE_MATCH, Map.of("nickname", clean)));
    }

    /** Valida el código con MATCH_CODE_REGEX y envía JOIN_MATCH. */
    public void joinMatch(String nickname, String code) {
        if (!isValidNickname(nickname)) {
            model.setError(nicknameError());
            return;
        }
        if (!isValidCode(code)) {
            model.setError("El código de la partida debe tener " + IConstants.MATCH_CODE_LENGTH
                    + " letras o números.");
            return;
        }
        String cleanNickname = nickname.trim();
        model.setNickname(cleanNickname);
        connection.send(Message.create(MessageType.JOIN_MATCH,
                Map.of("nickname", cleanNickname, "code", code.trim())));
    }

    /** Envía SELECT_ANIMAL. */
    public void selectAnimal(AnimalSpecies species) {
        if (species == null) {
            return;
        }
        connection.send(Message.create(MessageType.SELECT_ANIMAL, Map.of("species", species.name())));
    }

    /** model.reset() y setPhase(LOBBY). */
    public void backToLobby() {
        model.reset();
        model.setPhase(GamePhase.LOBBY);
    }

    /** Largo entre NICKNAME_MIN_LENGTH y NICKNAME_MAX_LENGTH. */
    private boolean isValidNickname(String nickname) {
        if (nickname == null) {
            return false;
        }
        int length = nickname.trim().length();
        return length >= IConstants.NICKNAME_MIN_LENGTH && length <= IConstants.NICKNAME_MAX_LENGTH;
    }

    private boolean isValidCode(String code) {
        return code != null && code.trim().matches(IConstants.MATCH_CODE_REGEX);
    }

    private String nicknameError() {
        return "El apodo debe tener entre " + IConstants.NICKNAME_MIN_LENGTH + " y "
                + IConstants.NICKNAME_MAX_LENGTH + " caracteres.";
    }
}
