package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import constants.IConstants;

/** Rectángulo de arrastre. */
public class SelectionBoxRenderer implements IDrawable {
    private Rectangle box; // en píxeles del panel; null si no se está arrastrando

    /** Fija el rectángulo entre dos esquinas (en cualquier orden). */
    public void setBox(Point start, Point end) {
        box = new Rectangle(
                Math.min(start.x, end.x), Math.min(start.y, end.y),
                Math.abs(end.x - start.x), Math.abs(end.y - start.y));
    }

    /** Quita el rectángulo. */
    public void clear() {
        box = null;
    }

    public Rectangle getBox() {
        return box == null ? null : new Rectangle(box);
    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        if (box == null) {
            return;
        }
        Composite oldComposite = g.getComposite();
        Stroke oldStroke = g.getStroke();
        g.setColor(IConstants.SELECTION_COLOR);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g.fillRect(box.x, box.y, box.width, box.height);
        g.setComposite(oldComposite);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(box.x, box.y, box.width, box.height);
        g.setStroke(oldStroke);
    }
}
