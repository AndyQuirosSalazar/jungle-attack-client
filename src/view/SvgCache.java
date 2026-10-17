package view;

import java.awt.image.BufferedImage;
import java.util.Map;

/** Singleton (holder idiom). Rasteriza los SVG con JSVG. */
public class SvgCache {

    /** La JVM crea la instancia una sola vez y de forma segura entre hilos. */
    private static class Holder {
        private static final SvgCache INSTANCE = new SvgCache();
    }

    private Map<String, BufferedImage> cache; // imágenes ya rasterizadas

    public static SvgCache getInstance() {
        return Holder.INSTANCE;
    }

    /** Rasteriza todos los SVG una sola vez al arrancar (JSVG). */
    public void preload() {
        // TODO
    }

    /** Devuelve la imagen del caché. */
    public BufferedImage get(String svgName, int size) {
        // TODO
        return null;
    }
}
