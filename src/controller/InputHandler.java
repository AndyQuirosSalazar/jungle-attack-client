package controller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Todas las coordenadas pasan por CoordinateMapper antes de llegar al controlador. */
public class InputHandler extends MouseAdapter {
    private GameController controller;
    private SelectionManager selectionManager;
    private Point dragStart; // inicio de la selección por arrastre

    /** Clic izquierdo: inicia selección. */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO
    }

    /** Actualiza el rectángulo que dibuja SelectionBoxRenderer. */
    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO
    }

    /** Clic izquierdo: termina el rectángulo de selección. */
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO
    }

    /** Clic derecho sobre enemigo/bot: atacar; clic derecho en camino: mover. */
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO
    }
}
