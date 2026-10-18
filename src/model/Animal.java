package model;

import static constants.IConstants.TELEPORT_THRESHOLD;

public class Animal extends Entity {
    private final AnimalSpecies species; // especie
    private final int packId;            // pack al que pertenece
    private final PackType packType;     // PLAYER, OPPONENT o BOT
    private int energy;                  // energía actual
    private boolean alive;               // está vivo
    private boolean selected;            // seleccionado por el jugador (solo local)
    private double prevX;                // posición del snapshot anterior (para interpolar)
    private double prevY;

    public Animal(int id, AnimalSpecies species, int packId, PackType packType,
                  int energy, double x, double y, boolean alive) {
        super(id, x, y);
        this.species = species;
        this.packId = packId;
        this.packType = packType;
        this.energy = energy;
        this.alive = alive;
        this.prevX = x; // evita interpolar desde (0,0) en el primer frame
        this.prevY = y;
    }

    /** prevX + (x - prevX) * alpha */
    public double getRenderX(double alpha) {
        return prevX + (x - prevX) * alpha;
    }

    public double getRenderY(double alpha) {
        return prevY + (y - prevY) * alpha;
    }

    /** Guarda la posición actual en prev y aplica la nueva. */
    public void applyState(int energy, double x, double y, boolean alive) {
        double dx = x - this.x;
        double dy = y - this.y;
        boolean teleported = Math.hypot(dx, dy) > TELEPORT_THRESHOLD;

        if (teleported || !alive) {
            // cambio de zona o muerte: sin interpolación
            this.prevX = x;
            this.prevY = y;
        } else {
            this.prevX = this.x;
            this.prevY = this.y;
        }

        setPosition(x, y);
        this.energy = energy;
        this.alive = alive;

        if (!alive) {
            this.selected = false; // un muerto no puede seguir seleccionado
        }
    }

    public boolean isAlive() { return alive; }

    /** packType == PLAYER */
    public boolean isOwn() { return packType == PackType.PLAYER; }

    public boolean isSelected() { return selected; }

    /** Un animal muerto no se puede seleccionar. */
    public void setSelected(boolean selected) { this.selected = selected && alive; }

    public AnimalSpecies getSpecies() { return species; }

    public int getPackId() { return packId; }

    public PackType getPackType() { return packType; }

    public int getEnergy() { return energy; }

    @Override
    public String getSvgName() {
        return species.getSvgFile();
    }
}