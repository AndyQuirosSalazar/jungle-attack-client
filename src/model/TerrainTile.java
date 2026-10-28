package model;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import constants.IConstants;

/**
 * Imagen de fondo de una zona según su posición en la cuadrícula del mapa (MAP_COLUMNS x MAP_ROWS).
 * Cada imagen solo tiene caminos hacia las zonas vecinas que existen; esos caminos (grupo "caminos"
 * del SVG) son los únicos lugares a donde se pueden mandar los animales.
 */
public enum TerrainTile {
    ESQUINA_SUPERIOR_IZQUIERDA("jungla_esquina_superior_izquierda.svg"),
    BORDE_SUPERIOR("jungla_borde_superior.svg"),
    ESQUINA_SUPERIOR_DERECHA("jungla_esquina_superior_derecha.svg"),
    BORDE_IZQUIERDO("jungla_borde_izquierdo.svg"),
    CENTRO("jungla_centro.svg"),
    BORDE_DERECHO("jungla_borde_derecho.svg"),
    ESQUINA_INFERIOR_IZQUIERDA("jungla_esquina_inferior_izquierda.svg"),
    BORDE_INFERIOR("jungla_borde_inferior.svg"),
    ESQUINA_INFERIOR_DERECHA("jungla_esquina_inferior_derecha.svg");

    private static final Pattern PATH = Pattern.compile("<path\\b[^>]*?\\bd=\"([^\"]+)\"[^>]*?stroke-width=\"([\\d.]+)\"");
    private static final Pattern CIRCLE = Pattern.compile(
            "<circle\\b[^>]*?cx=\"([\\d.-]+)\"[^>]*?cy=\"([\\d.-]+)\"[^>]*?\\br=\"([\\d.]+)\"");
    private static final double SNAP_STEP = 4; // separación entre anillos al buscar el camino más cercano
    private static final Pattern PATH_TOKEN = Pattern.compile("[MLCQZmlcqz]|-?\\d*\\.?\\d+(?:[eE]-?\\d+)?");

    private final String svgFile; // nombre del archivo SVG
    private Area road;            // forma de los caminos en unidades de zona; se lee la primera vez que se pide
    private boolean loaded;

    TerrainTile(String svgFile) {
        this.svgFile = svgFile;
    }

    public String getSvgFile() { return svgFile; }

    /** Imagen que le toca a la zona: fila = id / MAP_COLUMNS, columna = id % MAP_COLUMNS (igual que el servidor). */
    public static TerrainTile forZone(int zoneId) {
        if (zoneId < 0 || zoneId >= IConstants.ZONE_COUNT) {
            return null;
        }
        int column = zoneId % IConstants.MAP_COLUMNS;
        int row = zoneId / IConstants.MAP_COLUMNS;
        int tileColumn = column == 0 ? 0 : (column == IConstants.MAP_COLUMNS - 1 ? 2 : 1);
        int tileRow = row == 0 ? 0 : (row == IConstants.MAP_ROWS - 1 ? 2 : 1);
        return values()[tileRow * 3 + tileColumn];
    }

    /** true si el SVG se pudo leer y trae caminos (si no, la zona usa los segmentos del servidor). */
    public boolean hasRoad() { return getRoad() != null; }

    /** El punto (unidades de zona) cae dentro del camino dibujado, con su ancho real. */
    public boolean isOnRoad(double x, double y) {
        Area area = getRoad();
        return area != null && area.contains(x, y);
    }

    /**
     * Punto del camino más cercano a (x, y) a no más de maxDistance, buscando en anillos alrededor
     * del punto; {x, y} o null si no hay camino tan cerca.
     */
    public double[] nearestRoadPoint(double x, double y, double maxDistance) {
        Area area = getRoad();
        if (area == null) {
            return null;
        }
        if (area.contains(x, y)) {
            return new double[] {x, y};
        }
        for (double radius = SNAP_STEP; radius <= maxDistance; radius += SNAP_STEP) {
            int samples = Math.max(16, (int) (2 * Math.PI * radius / SNAP_STEP));
            for (int i = 0; i < samples; i++) {
                double angle = 2 * Math.PI * i / samples;
                double px = x + radius * Math.cos(angle);
                double py = y + radius * Math.sin(angle);
                if (area.contains(px, py)) {
                    // un poco más adentro para que el destino no quede justo en el borde
                    double inX = x + (radius + SNAP_STEP) * Math.cos(angle);
                    double inY = y + (radius + SNAP_STEP) * Math.sin(angle);
                    return area.contains(inX, inY) ? new double[] {inX, inY} : new double[] {px, py};
                }
            }
        }
        return null;
    }

    private synchronized Area getRoad() {
        if (!loaded) {
            loaded = true;
            road = loadRoad();
        }
        return road;
    }

    // ---------------------------------------------------------------- lectura del SVG

    /** Une en un Area cada <path> del grupo "caminos" (con su stroke-width) y la glorieta (<circle>). */
    private Area loadRoad() {
        String caminos = readCaminosGroup();
        if (caminos == null) {
            return null;
        }
        Area area = new Area();

        Matcher path = PATH.matcher(caminos);
        while (path.find()) {
            Shape line = parsePathData(path.group(1));
            float width = Float.parseFloat(path.group(2));
            BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            area.add(new Area(stroke.createStrokedShape(line)));
        }

        Matcher circle = CIRCLE.matcher(caminos);
        while (circle.find()) {
            double cx = Double.parseDouble(circle.group(1));
            double cy = Double.parseDouble(circle.group(2));
            double r = Double.parseDouble(circle.group(3));
            area.add(new Area(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2)));
        }

        if (area.isEmpty()) {
            System.err.println("TerrainTile: " + svgFile + " no trae caminos");
            return null;
        }
        return area;
    }

    /** Texto del grupo <g id="caminos"> ... </g>; el viewBox de los SVG es 0 0 ZONE_WIDTH ZONE_HEIGHT. */
    private String readCaminosGroup() {
        try (InputStream in = TerrainTile.class.getResourceAsStream(IConstants.ASSETS_PATH + svgFile)) {
            if (in == null) {
                System.err.println("TerrainTile: no se encontró " + IConstants.ASSETS_PATH + svgFile);
                return null;
            }
            String svg = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            int start = svg.indexOf("id=\"caminos\"");
            int end = start < 0 ? -1 : svg.indexOf("</g>", start);
            if (end < 0) {
                System.err.println("TerrainTile: " + svgFile + " no tiene el grupo caminos");
                return null;
            }
            return svg.substring(start, end);
        } catch (IOException e) {
            System.err.println("TerrainTile: no se pudo leer " + svgFile + ": " + e.getMessage());
            return null;
        }
    }

    /** Atributo d con comandos absolutos M, L, Q, C y Z (los que usan los SVG del mapa). */
    private static Shape parsePathData(String d) {
        List<String> tokens = new ArrayList<>();
        Matcher token = PATH_TOKEN.matcher(d);
        while (token.find()) {
            tokens.add(token.group());
        }

        Path2D.Double shape = new Path2D.Double();
        char command = 'M';
        int i = 0;
        while (i < tokens.size()) {
            if (Character.isLetter(tokens.get(i).charAt(0))) {
                command = Character.toUpperCase(tokens.get(i).charAt(0));
                i++;
                if (command == 'Z') {
                    shape.closePath();
                    continue;
                }
            }
            switch (command) {
                case 'M' -> {
                    shape.moveTo(num(tokens, i), num(tokens, i + 1));
                    i += 2;
                    command = 'L'; // pares extra después de un M son líneas
                }
                case 'L' -> {
                    shape.lineTo(num(tokens, i), num(tokens, i + 1));
                    i += 2;
                }
                case 'Q' -> {
                    shape.quadTo(num(tokens, i), num(tokens, i + 1), num(tokens, i + 2), num(tokens, i + 3));
                    i += 4;
                }
                case 'C' -> {
                    shape.curveTo(num(tokens, i), num(tokens, i + 1), num(tokens, i + 2),
                            num(tokens, i + 3), num(tokens, i + 4), num(tokens, i + 5));
                    i += 6;
                }
                default -> i++; // comando no soportado: se salta
            }
        }
        return shape;
    }

    private static double num(List<String> tokens, int index) {
        return index < tokens.size() ? Double.parseDouble(tokens.get(index)) : 0;
    }
}
