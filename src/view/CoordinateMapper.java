package view;

import constants.IConstants;

/**
 * El servidor manda coordenadas locales a la zona (0..ZONE_WIDTH, 0..ZONE_HEIGHT);
 * se escalan al tamaño del panel.
 */
public class CoordinateMapper {

    /** screenX * ZONE_WIDTH / panelWidth */
    public static double toWorldX(int screenX, int panelWidth) {
        if (panelWidth <= 0) {
            return 0; // panel sin tamaño todavía: evita dividir entre cero
        }
        return (double) screenX * IConstants.ZONE_WIDTH / panelWidth;
    }

    /** screenY * ZONE_HEIGHT / panelHeight */
    public static double toWorldY(int screenY, int panelHeight) {
        if (panelHeight <= 0) {
            return 0;
        }
        return (double) screenY * IConstants.ZONE_HEIGHT / panelHeight;
    }

    /** worldX * panelWidth / ZONE_WIDTH */
    public static int toScreenX(double worldX, int panelWidth) {
        return (int) Math.round(worldX * panelWidth / IConstants.ZONE_WIDTH);
    }

    /** worldY * panelHeight / ZONE_HEIGHT */
    public static int toScreenY(double worldY, int panelHeight) {
        return (int) Math.round(worldY * panelHeight / IConstants.ZONE_HEIGHT);
    }
}
