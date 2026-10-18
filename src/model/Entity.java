package model;

public abstract class Entity {
    protected final int id; // id asignado por el servidor
    protected double x;     // coordenada horizontal local a la zona (0 a ZONE_WIDTH)
    protected double y;     // coordenada vertical local a la zona (0 a ZONE_HEIGHT)

    protected Entity(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }

    public double getX() { return x; }

    public double getY() { return y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Cada entidad dice qué imagen usa (polimorfismo). */
    public abstract String getSvgName();

    /** Dos entidades son la misma si son del mismo tipo y tienen el mismo id del servidor. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Entity) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}