package controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import model.Animal;
import view.CoordinateMapper;
import view.SelectionBoxRenderer;

/** Todas las coordenadas pasan por CoordinateMapper antes de llegar al controlador. */
public class InputHandler extends MouseAdapter {
    private static final int DRAG_THRESHOLD = 5; // píxeles: menos que esto cuenta como un clic, no como arrastre

    private GameController controller;
    private SelectionManager selectionManager;
    private SelectionBoxRenderer selectionBox; // el rectángulo que se ve mientras se arrastra
    private Point dragStart; // inicio de la selección por arrastre

    public InputHandler(GameController controller, SelectionManager selectionManager,
                        SelectionBoxRenderer selectionBox) {
        this.controller = controller;
        this.selectionManager = selectionManager;
        this.selectionBox = selectionBox;
    }

    /**
     * Clic izquierdo: inicia selección. Clic derecho: la orden se da al presionar (no en mouseClicked,
     * que Swing descarta si el mouse se movió aunque sea un píxel entre presionar y soltar).
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            dragStart = e.getPoint();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            issueCommand(e);
        }
    }

    /** Actualiza el rectángulo que dibuja SelectionBoxRenderer. */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragStart == null) {
            return;
        }
        selectionBox.setBox(dragStart, e.getPoint());
        repaint(e);
    }

    /** Clic izquierdo: termina el rectángulo de selección. */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e) || dragStart == null) {
            return;
        }
        Point start = dragStart;
        Point end = e.getPoint();
        dragStart = null;
        selectionBox.clear();
        repaint(e);

        Component panel = e.getComponent();
        if (Math.abs(end.x - start.x) < DRAG_THRESHOLD && Math.abs(end.y - start.y) < DRAG_THRESHOLD) {
            // casi no se movió: es un clic simple (con Shift se agrega a la selección)
            double x = worldX(end.x, panel);
            double y = worldY(end.y, panel);
            if (!selectionManager.hasSelection() || selectionManager.findOwnAt(x, y) != null) {
                selectionManager.selectAt(x, y, e.isShiftDown());
                return;
            }
            // con animales seleccionados, un clic izquierdo sobre un enemigo también ataca
            Animal target = selectionManager.findEnemyAt(x, y);
            if (target != null) {
                controller.onAttackRequested(target.getId());
            } else {
                selectionManager.selectAt(x, y, e.isShiftDown());
            }
            return;
        }
        double x1 = worldX(start.x, panel);
        double y1 = worldY(start.y, panel);
        double x2 = worldX(end.x, panel);
        double y2 = worldY(end.y, panel);
        selectionManager.selectInRectangle(new Rectangle2D.Double(
                Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1)));
    }

    /** Clic derecho sobre enemigo/bot: atacar; clic derecho en camino (o cerca): mover. */
    private void issueCommand(MouseEvent e) {
        Component panel = e.getComponent();
        double x = worldX(e.getX(), panel);
        double y = worldY(e.getY(), panel);
        Animal target = selectionManager.findEnemyAt(x, y);
        if (target != null) {
            controller.onAttackRequested(target.getId());
        } else {
            controller.onMoveRequested(x, y);
        }
    }

    private static double worldX(int screenX, Component panel) {
        return CoordinateMapper.toWorldX(screenX, panel.getWidth());
    }

    private static double worldY(int screenY, Component panel) {
        return CoordinateMapper.toWorldY(screenY, panel.getHeight());
    }

    private static void repaint(MouseEvent e) {
        if (e.getComponent() != null) {
            e.getComponent().repaint();
        }
    }
}
