package model;

import java.util.List;

public class Pack {
    private int id;                     // id del pack
    private AnimalSpecies species;      // especie del pack
    private PackType type;              // PLAYER, OPPONENT o BOT
    private int damageCapacity;         // daño compartido por todos los miembros
    private List<Animal> members; // animales del pack

    /** Solo los vivos. */
    public List<Animal> getLivingMembers() {
        // TODO
        return null;
    }

    public int countAlive() {
        // TODO
        return 0;
    }
}
