package view;

import java.awt.Graphics2D;

public interface IDrawable {
    /** Cada elemento sabe dibujarse (polimorfismo). */
    void draw(Graphics2D g, double alpha);
}
