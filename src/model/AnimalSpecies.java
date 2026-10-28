package model;

import java.util.Locale;

public enum AnimalSpecies {
    JAGUAR("Jaguar", "jaguar.svg"),
    SLOTH("Perezoso", "sloth.svg"),
    SCARLET_MACAW("Lapa roja", "scarlet_macaw.svg"),
    WHITE_FACED_MONKEY("Mono Carablanca", "white_faced_monkey.svg"),
    TOUCAN("Tucán", "toucan.svg"),
    RED_EYED_TREE_FROG("Rana ", "red_eyed_tree_frog.svg"),
    COATI("Pizote", "coati.svg");

    private final String displayName; // nombre para mostrar
    private final String svgFile;     // nombre del archivo SVG

    AnimalSpecies(String displayName, String svgFile) {
        this.displayName = displayName;
        this.svgFile = svgFile;
    }

    public String getDisplayName() { return displayName; }

    public String getSvgFile() { return svgFile; }

    /**
     * Convierte el texto del JSON a la especie.
     * Devuelve null si el texto viene vacío o no corresponde a ninguna especie
     * (por ejemplo, opponentChoice cuando el rival aún no ha elegido).
     */
    public static AnimalSpecies fromName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null; // nombre desconocido
        }
    }
}
