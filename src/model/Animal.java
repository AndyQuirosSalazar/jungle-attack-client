package model;

public class Animal extends Entity {
    private AnimalSpecies species; // especie
    private int packId;            // pack al que pertenece
    private PackType packType;     // PLAYER, OPPONENT o BOT
    private int energy;            // energía actual
    private boolean alive;         // está vivo
    private boolean selected;      // seleccionado por el jugador (solo local)
    private double prevX;          // posición del snapshot anterior (para interpolar)
    private double prevY;

    /** prevX + (x - prevX) * alpha */
    public double getRenderX(double alpha) {
        // TODO
        return 0;
    }

    public double getRenderY(double alpha) {
        // TODO
        return 0;
    }

    /** Guarda la posición actual en prev y aplica la nueva. */
    public void applyState(int energy, double x, double y, boolean alive) {
        // TODO
    }

    public boolean isAlive() { return alive; }

    /** packType == PLAYER */
    public boolean isOwn() { return packType == PackType.PLAYER; }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public String getSvgName() {
        // TODO: devuelve el SVG de su especie
        return null;
    }
}
