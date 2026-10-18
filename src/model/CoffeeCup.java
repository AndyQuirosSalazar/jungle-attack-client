package model;

public class CoffeeCup extends Entity {

    public CoffeeCup(int id, double x, double y) {
        super(id, x, y);
    }

    @Override
    public String getSvgName() {
        // devuelve el SVG de la taza de café
        return "coffee_cup.svg";
    }
}
