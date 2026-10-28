package model;

public class PathSegment {
    private final double x1, y1; // punto inicial
    private final double x2, y2; // punto final

    /** Se arma en applyGameStarted a partir de los caminos que manda el servidor. */
    public PathSegment(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /** Para PathRenderer. */
    public double getX1() { return x1; }

    public double getY1() { return y1; }

    public double getX2() { return x2; }

    public double getY2() { return y2; }

    /** Distancia del punto al segmento <= tolerance (valida clics de destino). */
    public boolean contains(double x, double y, double tolerance) {
        return distanceSquaredTo(x, y) <= tolerance * tolerance;
    }

    /** Distancia al cuadrado del punto al punto más cercano del segmento. */
    public double distanceSquaredTo(double x, double y) {
        double[] closest = closestPoint(x, y);
        double ex = x - closest[0];
        double ey = y - closest[1];
        return ex * ex + ey * ey;
    }

    /** Punto del segmento más cercano a (x, y), como {x, y}. */
    public double[] closestPoint(double x, double y) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lengthSquared = dx * dx + dy * dy;

        // segmento degenerado: los dos extremos coinciden
        if (lengthSquared == 0) {
            return new double[] {x1, y1};
        }

        // proyección del punto sobre la recta, acotada a [0, 1] para quedarse dentro del segmento
        double t = ((x - x1) * dx + (y - y1) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t));
        return new double[] {x1 + t * dx, y1 + t * dy};
    }
}
