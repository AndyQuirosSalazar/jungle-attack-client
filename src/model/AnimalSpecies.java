package model;

public enum AnimalSpecies {
    JAGUAR("Jaguar", "jaguar.svg"),
    SLOTH("Perezoso", "sloth.svg"),
    SCARLET_MACAW("Lapa roja", "scarlet_macaw.svg"),
    WHITE_FACED_MONKEY("Mono cariblanco", "white_faced_monkey.svg"),
    TOUCAN("Tucán", "toucan.svg"),
    RED_EYED_TREE_FROG("Rana calzonuda", "red_eyed_tree_frog.svg"),
    COATI("Pizote", "coati.svg");

    private final String displayName; // nombre para mostrar
    private final String svgFile;     // nombre del archivo SVG

    AnimalSpecies(String displayName, String svgFile) {
        this.displayName = displayName;
        this.svgFile = svgFile;
    }

    public String getDisplayName() { return displayName; }

    public String getSvgFile() { return svgFile; }

    /** Convierte el texto del JSON a la especie. */
    public static AnimalSpecies fromName(String name) {
        // TODO
        return null;
    }
}
