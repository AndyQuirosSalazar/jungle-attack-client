package view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import constants.IConstants;
import model.CoffeeCup;

/** Taza de café sobre el camino. */
public class CupSprite implements IDrawable {
    private static final int CUP_SIZE = IConstants.SPRITE_SIZE * 3 / 4; // un poco más chica que un animal

    private CoffeeCup cup;
    private Component panel; // de aquí sale el tamaño actual para pasar de unidades del servidor a píxeles

    public CupSprite(CoffeeCup cup, Component panel) {
        this.cup = cup;
        this.panel = panel;
    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        int centerX = CoordinateMapper.toScreenX(cup.getX(), panel.getWidth());
        int centerY = CoordinateMapper.toScreenY(cup.getY(), panel.getHeight());
        BufferedImage image = SvgCache.getInstance().get(cup.getSvgName(), CUP_SIZE);
        g.drawImage(image, centerX - CUP_SIZE / 2, centerY - CUP_SIZE / 2, null);
    }
}
