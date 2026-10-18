package model;

public class Flag extends Entity {
    private final PackType owner; // de quién es la bandera

    public Flag(int id, PackType owner, double x, double y) {
        super(id, x, y);
        this.owner = owner;
    }

    @Override
    public String getSvgName() {
        // TODO: devuelve el SVG de la bandera
        return null;
    }
}
