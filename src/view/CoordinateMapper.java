package view;

/**
 * El servidor manda coordenadas locales a la zona (0..ZONE_WIDTH, 0..ZONE_HEIGHT);
 * se escalan al tamaño del panel.
 */
public class CoordinateMapper {

    /** screenX * ZONE_WIDTH / panelWidth */
    public static double toWorldX(int screenX, int panelWidth) {
        // TODO
        return 0;
    }

    /** screenY * ZONE_HEIGHT / panelHeight */
    public static double toWorldY(int screenY, int panelHeight) {
        // TODO
        return 0;
    }

    /** worldX * panelWidth / ZONE_WIDTH */
    public static int toScreenX(double worldX, int panelWidth) {
        // TODO
        return 0;
    }

    /** worldY * panelHeight / ZONE_HEIGHT */
    public static int toScreenY(double worldY, int panelHeight) {
        // TODO
        return 0;
    }
}
