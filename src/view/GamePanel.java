package view;

import java.awt.Graphics;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.GameModel;
import model.IGameObserver;
import model.ModelChange;

public class GamePanel extends JPanel implements IGameObserver {
    private GameModel model;
    private Timer animationTimer;          // cada FRAME_DELAY_MS: repaint()
    private List<IDrawable> drawables;     // lo que se dibuja en el cuadro actual

    /** alpha = model.getInterpolationAlpha(System.nanoTime()); pinta fondo, caminos, tazas, bandera, animales, selección. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO
    }

    /** Arma la lista a partir de la zona actual del modelo. */
    private void buildDrawables() {
        // TODO
    }

    /** STATE_UPDATED o ZONE_CHANGED -> buildDrawables; dispara animaciones según model.getLastEvents(). */
    @Override
    public void onModelChanged(ModelChange change) {
        // TODO
    }
}
