package controller;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import constants.IConstants;
import model.Animal;
import model.Entity;
import model.GameModel;
import model.Zone;

/** Las coordenadas que recibe son locales a la zona (ya pasadas por CoordinateMapper). */
public class SelectionManager {
    private GameModel model;

    public SelectionManager(GameModel model) {
        this.model = model;
    }

    /** Clic simple sobre un animal propio (con Shift agrega a la selección). */
    public void selectAt(double x, double y, boolean addToSelection) {
        Animal animal = findNearest(x, y, true);
        if (animal == null) {
            if (!addToSelection) {
                clearSelection(); // un clic en vacío (sin Shift) deselecciona todo
            }
            return;
        }
        List<Animal> selection = new ArrayList<>();
        if (addToSelection) {
            selection.addAll(model.getSelectedAnimals());
        }
        if (!selection.contains(animal)) {
            selection.add(animal);
        }
        model.setSelectedAnimals(selection);
    }

    /** Selección por arrastre, solo animales propios y vivos. */
    public void selectInRectangle(Rectangle2D area) {
        List<Animal> selection = new ArrayList<>();
        if (area != null) {
            for (Animal animal : animalsInCurrentZone()) {
                if (animal.isOwn() && animal.isAlive() && area.contains(animal.getX(), animal.getY())) {
                    selection.add(animal);
                }
            }
        }
        model.setSelectedAnimals(selection);
    }

    public void clearSelection() {
        if (model.getSelectedAnimals().isEmpty()) {
            return; // nada que cambiar: no se avisa a las vistas
        }
        model.setSelectedAnimals(new ArrayList<>());
    }

    /** Quita de la selección los muertos o los que ya no están en la zona actual (en cada STATE_UPDATE). */
    public void pruneSelection() {
        List<Animal> current = animalsInCurrentZone();
        List<Animal> kept = new ArrayList<>();
        for (Animal animal : model.getSelectedAnimals()) {
            if (animal.isAlive() && animal.isOwn() && current.contains(animal)) {
                kept.add(animal);
            }
        }
        if (kept.size() != model.getSelectedAnimals().size()) {
            model.setSelectedAnimals(kept);
        }
    }

    public List<Integer> getSelectedIds() {
        List<Integer> ids = new ArrayList<>();
        for (Animal animal : model.getSelectedAnimals()) {
            ids.add(animal.getId());
        }
        return ids;
    }

    /** Enemigo más cercano dentro de ATTACK_CLICK_TOLERANCE (más amplio que el de selección). */
    public Animal findEnemyAt(double x, double y) {
        return findNearest(x, y, false, IConstants.ATTACK_CLICK_TOLERANCE);
    }

    private Animal findNearest(double x, double y, boolean own) {
        return findNearest(x, y, own, IConstants.CLICK_TOLERANCE);
    }

    /** El animal vivo (propio si own, enemigo si no) más cercano al punto y a ≤ tolerance; null si no hay ninguno. */
    private Animal findNearest(double x, double y, boolean own, double tolerance) {
        Animal nearest = null;
        double nearestDistance = tolerance;
        double alpha = model.getInterpolationAlpha(System.nanoTime()); // el clic cae donde se dibuja el animal
        for (Animal animal : animalsInCurrentZone()) {
            if (!animal.isAlive() || animal.isOwn() != own) {
                continue;
            }
            double distance = Math.hypot(animal.getRenderX(alpha) - x, animal.getRenderY(alpha) - y);
            if (distance <= nearestDistance) {
                nearest = animal;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private List<Animal> animalsInCurrentZone() {
        List<Animal> animals = new ArrayList<>();
        Zone zone = model.getCurrentZone();
        if (zone != null) {
            for (Entity entity : zone.getEntities()) {
                if (entity instanceof Animal animal) {
                    animals.add(animal);
                }
            }
        }
        return animals;
    }
}
