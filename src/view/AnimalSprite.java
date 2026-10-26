package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import constants.IConstants;
import model.Animal;
import model.PackType;

/** Imagen SVG + barra de energía + resaltado si está seleccionado + color del pack. */
public class AnimalSprite implements IDrawable {
    private static final int BOT_MAX_ENERGY = 10;   // igual que la energía de un bot en el servidor
    private static final int PACK_RING_MARGIN = 2;  // el aro del pack queda 2 px fuera del dibujo
    private static final int SELECTION_RING_MARGIN = 5;
    private static final int BAR_GAP = 3;           // espacio entre el aro y la barra de energía
    private static final Color BAR_BACKGROUND = new Color(40, 40, 40);

    private Animal animal;
    private Component panel; // de aquí sale el tamaño actual para pasar de unidades del servidor a píxeles

    public AnimalSprite(Animal animal, Component panel) {
        this.animal = animal;
        this.panel = panel;
    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        if (!animal.isAlive()) {
            return; // los muertos no se dibujan
        }
        int centerX = CoordinateMapper.toScreenX(animal.getRenderX(alpha), panel.getWidth());
        int centerY = CoordinateMapper.toScreenY(animal.getRenderY(alpha), panel.getHeight());
        int half = IConstants.SPRITE_SIZE / 2;

        Stroke oldStroke = g.getStroke();
        Object oldAntialiasing = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1) resaltado si está seleccionado (el aro más grande)
        if (animal.isSelected()) {
            drawRing(g, centerX, centerY, half + SELECTION_RING_MARGIN, 3f, IConstants.SELECTION_COLOR);
        }
        // 2) aro con el color del pack
        drawRing(g, centerX, centerY, half + PACK_RING_MARGIN, 2f, packColor(animal.getPackType()));
        // 3) imagen del animal
        BufferedImage image = SvgCache.getInstance().get(animal.getSvgName(), IConstants.SPRITE_SIZE);
        g.drawImage(image, centerX - half, centerY - half, null);
        // 4) barra de energía arriba
        drawEnergyBar(g, centerX, centerY);

        g.setStroke(oldStroke);
        if (oldAntialiasing != null) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasing);
        }
    }

    private void drawRing(Graphics2D g, int cx, int cy, int radius, float thickness, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        g.draw(new Ellipse2D.Double(cx - radius, cy - radius, radius * 2.0, radius * 2.0));
    }

    private void drawEnergyBar(Graphics2D g, int cx, int cy) {
        int width = IConstants.SPRITE_SIZE;
        int height = IConstants.ENERGY_BAR_HEIGHT;
        int x = cx - width / 2;
        int y = cy - IConstants.SPRITE_SIZE / 2 - PACK_RING_MARGIN - BAR_GAP - height;

        int maxEnergy = animal.getPackType() == PackType.BOT ? BOT_MAX_ENERGY : IConstants.MAX_PLAYER_ENERGY;
        double ratio = Math.max(0.0, Math.min(1.0, animal.getEnergy() / (double) maxEnergy));

        g.setColor(BAR_BACKGROUND);
        g.fillRect(x, y, width, height);
        g.setColor(energyColor(ratio));
        g.fillRect(x, y, (int) Math.round(width * ratio), height);
    }

    /** Verde con mucha energía, amarillo con la mitad o menos, rojo cuando queda poca. */
    private Color energyColor(double ratio) {
        if (ratio > 0.5) {
            return IConstants.PLAYER_COLOR;
        }
        return ratio > 0.25 ? new Color(240, 180, 0) : IConstants.OPPONENT_COLOR;
    }

    private Color packColor(PackType type) {
        return switch (type) {
            case PLAYER -> IConstants.PLAYER_COLOR;
            case OPPONENT -> IConstants.OPPONENT_COLOR;
            case BOT -> IConstants.BOT_COLOR;
        };
    }
}
