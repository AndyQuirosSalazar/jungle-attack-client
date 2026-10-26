package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import constants.IConstants;
import model.Flag;
import model.PackType;

/** Bandera con el color de su dueño. */
public class FlagSprite implements IDrawable {
    private static final int BASE_MARGIN = 4; // la base de color sobresale 4 px del dibujo

    private Flag flag;
    private Component panel; // de aquí sale el tamaño actual para pasar de unidades del servidor a píxeles

    public FlagSprite(Flag flag, Component panel) {
        this.flag = flag;
        this.panel = panel;
    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        int centerX = CoordinateMapper.toScreenX(flag.getX(), panel.getWidth());
        int centerY = CoordinateMapper.toScreenY(flag.getY(), panel.getHeight());
        int half = IConstants.SPRITE_SIZE / 2;
        int radius = half + BASE_MARGIN;
        Color ownerColor = ownerColor(flag.getOwner());

        Composite oldComposite = g.getComposite();
        Stroke oldStroke = g.getStroke();
        Object oldAntialiasing = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // base con el color del dueño (relleno suave + aro firme), para que se sepa de quién es la bandera
        Ellipse2D base = new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0);
        g.setColor(ownerColor);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.fill(base);
        g.setComposite(oldComposite);
        g.setStroke(new BasicStroke(2.5f));
        g.draw(base);

        // la bandera encima
        BufferedImage image = SvgCache.getInstance().get(flag.getSvgName(), IConstants.SPRITE_SIZE);
        g.drawImage(image, centerX - half, centerY - half, null);

        g.setStroke(oldStroke);
        if (oldAntialiasing != null) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasing);
        }
    }

    private Color ownerColor(PackType owner) {
        return switch (owner) {
            case PLAYER -> IConstants.PLAYER_COLOR;
            case OPPONENT -> IConstants.OPPONENT_COLOR;
            case BOT -> IConstants.BOT_COLOR;
        };
    }
}
