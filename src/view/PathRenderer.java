package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import model.PathSegment;
import model.Zone;

/** Dibuja los caminos de la zona. */
public class PathRenderer implements IDrawable {
    private static final Color EDGE_COLOR = new Color(120, 90, 55);   // borde del camino
    private static final Color ROAD_COLOR = new Color(205, 175, 120); // centro del camino
    private static final float EDGE_WIDTH = 14f;
    private static final float ROAD_WIDTH = 10f;

    private Zone zone;
    private Component panel; // de aquí sale el tamaño actual para pasar de unidades del servidor a píxeles

    public PathRenderer(Zone zone, Component panel) {
        this.zone = zone;
        this.panel = panel;
    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        Stroke oldStroke = g.getStroke();
        Object oldAntialiasing = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // primero todos los bordes y después todos los centros: así en los cruces los caminos se funden
        paintAll(g, EDGE_COLOR, EDGE_WIDTH);
        paintAll(g, ROAD_COLOR, ROAD_WIDTH);

        g.setStroke(oldStroke);
        if (oldAntialiasing != null) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasing);
        }
    }

    private void paintAll(Graphics2D g, Color color, float width) {
        g.setColor(color);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int w = panel.getWidth();
        int h = panel.getHeight();
        for (PathSegment path : zone.getPaths()) {
            g.draw(new Line2D.Double(
                    CoordinateMapper.toScreenX(path.getX1(), w), CoordinateMapper.toScreenY(path.getY1(), h),
                    CoordinateMapper.toScreenX(path.getX2(), w), CoordinateMapper.toScreenY(path.getY2(), h)));
        }
    }
}
