package model;

public class Flag extends Entity {
    private final PackType owner; // de quién es la bandera

    public Flag(int id, PackType owner, double x, double y) {
        super(id, x, y);
        this.owner = owner;
    }

    public PackType getOwner() { return owner; }

    @Override
    public String getSvgName() {
        // devuelve el SVG de la bandera
        return "flag.svg";
    }
}
