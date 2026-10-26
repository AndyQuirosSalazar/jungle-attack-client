package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

import constants.IConstants;
import controller.InputHandler;
import model.Animal;
import model.CoffeeCup;
import model.Entity;
import model.Flag;
import model.GameEvent;
import model.GameModel;
import model.IGameObserver;
import model.ModelChange;
import model.Zone;

public class GamePanel extends JPanel implements IGameObserver {
    private static final Color BACKGROUND = new Color(30, 78, 46); // verde selva

    private GameModel model;
    private Timer animationTimer;          // cada FRAME_DELAY_MS: repaint()
    private List<IDrawable> drawables;     // lo que se dibuja en el cuadro actual
    private List<Effect> effects;          // destellos cortos (golpes, muertes, tazas)
    private SelectionBoxRenderer selectionBox; // el rectángulo de arrastre, siempre encima de todo

    public GamePanel(GameModel model, InputHandler inputHandler, SelectionBoxRenderer selectionBox) {
        this.model = model;
        this.selectionBox = selectionBox;
        this.drawables = new ArrayList<>();
        this.effects = new ArrayList<>();
        setBackground(BACKGROUND);

        if (inputHandler != null) {
            addMouseListener(inputHandler);
            addMouseMotionListener(inputHandler);
        }
        model.addObserver(this);
        buildDrawables();

        animationTimer = new Timer(IConstants.FRAME_DELAY_MS, e -> repaint());
        animationTimer.start();
    }

    /** Deja de dibujar y de escuchar al modelo (al cerrar la ventana). */
    public void dispose() {
        animationTimer.stop();
        model.removeObserver(this);
    }

    /** alpha = model.getInterpolationAlpha(System.nanoTime()); pinta fondo, caminos, tazas, bandera, animales, selección. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BACKGROUND);
            g2.fillRect(0, 0, getWidth(), getHeight());

            double alpha = model.getInterpolationAlpha(System.nanoTime());
            for (IDrawable drawable : drawables) {
                drawable.draw(g2, alpha);
            }
            effects.removeIf(Effect::isExpired);
            for (Effect effect : effects) {
                effect.draw(g2, alpha);
            }
            if (selectionBox != null) {
                selectionBox.draw(g2, alpha);
            }
        } finally {
            g2.dispose();
        }
    }

    /** Arma la lista a partir de la zona actual del modelo. */
    private void buildDrawables() {
        List<IDrawable> built = new ArrayList<>();
        Zone zone = model.getCurrentZone();
        if (zone != null) {
            built.add(new PathRenderer(zone, this));
            // el modelo entrega las entidades ya en orden de dibujo: tazas, banderas y animales
            for (Entity entity : zone.getEntities()) {
                if (entity instanceof CoffeeCup cup) {
                    built.add(new CupSprite(cup, this));
                } else if (entity instanceof Flag flag) {
                    built.add(new FlagSprite(flag, this));
                } else if (entity instanceof Animal animal) {
                    built.add(new AnimalSprite(animal, this));
                }
            }
        }
        drawables = built;
    }

    /** STATE_UPDATED o ZONE_CHANGED -> buildDrawables; dispara animaciones según model.getLastEvents(). */
    @Override
    public void onModelChanged(ModelChange change) {
        switch (change) {
            case STATE_UPDATED -> {
                buildDrawables();
                startEffects(model.getLastEvents());
            }
            case ZONE_CHANGED, PHASE_CHANGED -> {
                effects.clear(); // los destellos eran de otra zona
                buildDrawables();
            }
            default -> {
                return; // la selección, los errores, etc. no cambian lo que se dibuja
            }
        }
        repaint();
    }

    // ---------------------------------------------------------------- animaciones

    private void startEffects(List<GameEvent> events) {
        for (GameEvent event : events) {
            Animal source = findAnimal(event.getSourceId());
            Animal target = findAnimal(event.getTargetId());
            switch (event.getType()) {
                case ATTACK -> {
                    if (target != null) {
                        effects.add(newHitEffect(source, target));
                    }
                }
                case ANIMAL_DIED -> {
                    if (target != null) {
                        effects.add(newRingEffect(target, new Color(235, 235, 235), 10, 34, 600));
                    }
                }
                case CUP_COLLECTED -> {
                    if (source != null) {
                        effects.add(newRingEffect(source, new Color(255, 214, 0), 8, 26, 500));
                    }
                }
                case BOT_KILLED -> {
                    if (target != null) {
                        effects.add(newRingEffect(target, new Color(255, 140, 0), 12, 40, 700));
                    }
                }
            }
        }
    }

    /** Busca un animal por id en la zona que se está viendo (los eventos solo hablan de esa zona). */
    private Animal findAnimal(int id) {
        Zone zone = model.getCurrentZone();
        if (zone != null) {
            for (Entity entity : zone.getEntities()) {
                if (entity instanceof Animal animal && animal.getId() == id) {
                    return animal;
                }
            }
        }
        return null;
    }

    /** Golpe: una línea del atacante al objetivo y un aro rojo sobre el objetivo. */
    private Effect newHitEffect(Animal attacker, Animal target) {
        double fromX = attacker == null ? target.getX() : attacker.getX();
        double fromY = attacker == null ? target.getY() : attacker.getY();
        return new Effect(new Color(255, 80, 40), fromX, fromY, target.getX(), target.getY(),
                attacker != null, 5, 16, 250);
    }

    /** Un aro que crece desde el animal y se desvanece. */
    private Effect newRingEffect(Animal at, Color color, double fromRadius, double toRadius, long durationMs) {
        return new Effect(color, at.getX(), at.getY(), at.getX(), at.getY(), false, fromRadius, toRadius, durationMs);
    }

    /** Un destello corto: un aro que crece y se desvanece, o una línea de golpe. */
    private class Effect implements IDrawable {
        private final Color color;
        private final double x1, y1;     // origen en unidades del servidor (para la línea de golpe)
        private final double x2, y2;     // dónde ocurre
        private final boolean line;      // true: golpe con línea del atacante al objetivo
        private final double fromRadius, toRadius;
        private final long startNanos = System.nanoTime();
        private final long durationNanos;

        private Effect(Color color, double x1, double y1, double x2, double y2, boolean line,
                       double fromRadius, double toRadius, long durationMs) {
            this.color = color;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.line = line;
            this.fromRadius = fromRadius;
            this.toRadius = toRadius;
            this.durationNanos = durationMs * 1_000_000L;
        }

        boolean isExpired() {
            return System.nanoTime() - startNanos >= durationNanos;
        }

        @Override
        public void draw(Graphics2D g, double alpha) {
            double progress = Math.min(1.0, (System.nanoTime() - startNanos) / (double) durationNanos);
            float opacity = (float) Math.max(0.0, 1.0 - progress);
            int w = getWidth();
            int h = getHeight();
            int px = CoordinateMapper.toScreenX(x2, w);
            int py = CoordinateMapper.toScreenY(y2, h);

            Composite oldComposite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g.setColor(color);
            if (line) {
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Double(CoordinateMapper.toScreenX(x1, w), CoordinateMapper.toScreenY(y1, h), px, py));
            }
            double radius = fromRadius + (toRadius - fromRadius) * progress;
            g.setStroke(new BasicStroke(2.5f));
            g.draw(new Ellipse2D.Double(px - radius, py - radius, radius * 2, radius * 2));
            g.setComposite(oldComposite);
        }
    }
}
