package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pack {
    private int id;                     // id del pack
    private AnimalSpecies species;      // especie del pack
    private PackType type;              // PLAYER, OPPONENT o BOT
    private int damageCapacity;         // daño compartido por todos los miembros
    private List<Animal> members; // animales del pack

    public Pack(int id, AnimalSpecies species, PackType type) {
        this.id = id;
        this.species = species;
        this.type = type;
        this.members = new ArrayList<>();
    }

    public int getId() { return id; }

    public AnimalSpecies getSpecies() { return species; }

    public PackType getType() { return type; }

    public int getDamageCapacity() { return damageCapacity; }

    public void setDamageCapacity(int damageCapacity) { this.damageCapacity = damageCapacity; }

    public List<Animal> getMembers() { return Collections.unmodifiableList(members); }

    public void addMember(Animal animal) {
        if (animal != null && !members.contains(animal)) {
            members.add(animal);
        }
    }

    /** null si el animal aún no se conoce. */
    public Animal findMember(int animalId) {
        for (Animal animal : members) {
            if (animal.getId() == animalId) {
                return animal;
            }
        }
        return null;
    }

    /** Solo los vivos. */
    public List<Animal> getLivingMembers() {
        List<Animal> living = new ArrayList<>();
        for (Animal animal : members) {
            if (animal.isAlive()) {
                living.add(animal);
            }
        }
        return living;
    }

    public int countAlive() {
        int count = 0;
        for (Animal animal : members) {
            if (animal.isAlive()) {
                count++;
            }
        }
        return count;
    }
}
