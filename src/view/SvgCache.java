package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;

import constants.IConstants;
import model.AnimalSpecies;

/** Singleton (holder idiom). Rasteriza los SVG con JSVG. */
public class SvgCache {

    /** La JVM crea la instancia una sola vez y de forma segura entre hilos. */
    private static class Holder {
        private static final SvgCache INSTANCE = new SvgCache();
    }

    private static final String CUP_SVG = "coffee_cup.svg";
    private static final String FLAG_SVG = "flag.svg";

    private Map<String, BufferedImage> cache = new ConcurrentHashMap<>(); // imágenes ya rasterizadas
    private Map<String, BufferedImage> backgrounds = new ConcurrentHashMap<>(); // fondos: solo el último tamaño pedido

    public static SvgCache getInstance() {
        return Holder.INSTANCE;
    }

    /** Rasteriza todos los SVG una sola vez al arrancar (JSVG). */
    public void preload() {
        for (AnimalSpecies species : AnimalSpecies.values()) {
            get(species.getSvgFile(), IConstants.SPRITE_SIZE);
        }
        get(CUP_SVG, IConstants.SPRITE_SIZE);
        get(FLAG_SVG, IConstants.SPRITE_SIZE);
    }

    /** Devuelve la imagen del caché (si ese SVG no se había pedido con ese tamaño, se rasteriza en ese momento). */
    public BufferedImage get(String svgName, int size) {
        int safeSize = Math.max(1, size);
        return cache.computeIfAbsent(svgName + "@" + safeSize, key -> rasterize(svgName, safeSize));
    }

    /**
     * Fondo de zona estirado a width x height, igual que CoordinateMapper escala x e y por separado
     * (así los caminos de la imagen quedan donde el modelo los valida). Se guarda solo el último tamaño
     * para no llenar la memoria mientras se redimensiona la ventana. Devuelve null si el SVG no se pudo leer.
     */
    public BufferedImage getBackground(String svgName, int width, int height) {
        BufferedImage current = backgrounds.get(svgName);
        if (current != null && current.getWidth() == width && current.getHeight() == height) {
            return current;
        }
        if (width <= 0 || height <= 0) {
            return null;
        }
        SVGDocument document = load(svgName);
        if (document == null) {
            return null;
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            Dimension2D natural = document.size();
            g.scale(width / natural.getWidth(), height / natural.getHeight());
            document.render(null, g);
        } finally {
            g.dispose();
        }
        backgrounds.put(svgName, image);
        return image;
    }

    /** SVG -> imagen cuadrada de size x size, conservando la proporción y centrada. */
    private BufferedImage rasterize(String svgName, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            SVGDocument document = load(svgName);
            if (document == null) {
                drawPlaceholder(g, size); // el archivo falta o no se pudo leer: se dibuja algo visible, sin romper el juego
                return image;
            }
            Dimension2D natural = document.size(); // tamaño original del SVG
            double scale = Math.min(size / natural.getWidth(), size / natural.getHeight());
            g.translate((size - natural.getWidth() * scale) / 2.0, (size - natural.getHeight() * scale) / 2.0);
            g.scale(scale, scale);
            document.render(null, g);
        } finally {
            g.dispose();
        }
        return image;
    }

    private SVGDocument load(String svgName) {
        if (svgName == null) {
            return null;
        }
        URL url = SvgCache.class.getResource(IConstants.ASSETS_PATH + svgName);
        if (url == null) {
            System.err.println("SvgCache: no se encontró " + IConstants.ASSETS_PATH + svgName);
            return null;
        }
        SVGDocument document = new SVGLoader().load(url);
        if (document == null || document.size().getWidth() <= 0 || document.size().getHeight() <= 0) {
            System.err.println("SvgCache: no se pudo leer " + svgName);
            return null;
        }
        return document;
    }

    private void drawPlaceholder(Graphics2D g, int size) {
        int margin = Math.max(1, size / 8);
        g.setColor(new Color(200, 200, 200));
        g.fillOval(margin, margin, size - 2 * margin, size - 2 * margin);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(margin, margin, size - 2 * margin - 1, size - 2 * margin - 1);
    }
}
