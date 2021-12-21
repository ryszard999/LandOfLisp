package oop.lisp.mapelement;

import oop.lisp.additional.*;
import oop.lisp.map.RectangularJungle;

import java.util.ArrayList;

public class Animal {
    private MapDirection direction;
    private Vector2d position;
    private final RectangularJungle map;
    private int energy;
    public final int moveEnergy, startEnergy;
    private final Genotype genotype;
    private final ArrayList<IPositionChangeObserver> observers = new ArrayList<IPositionChangeObserver>();

    // This constructor is used when we add 'random' initial Animal to the map
    public Animal(RectangularJungle map, Vector2d initialPosition, int startEnergy, int moveEnergy) {
        this.map = map;
        this.position = initialPosition;
        this.direction = MapDirection.randomDirection();
        this.genotype = new Genotype();
        this.energy = startEnergy;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
    }

    // This one is used when the new Animal is born to its parents
    public Animal(RectangularJungle map, Vector2d initialPosition, int startEnergy, int moveEnergy, Genotype genotype) {
        this.map = map;
        this.position = initialPosition;
        this.direction = MapDirection.randomDirection();
        this.energy = startEnergy;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.genotype = genotype;
    }

    public void move() {
        int moveID = genotype.getGene();

        if (moveID == 0 || moveID == 4) {
            Vector2d oldPosition = position;
            Vector2d newPosition = (moveID == 0) ? map.moveTo( position.add(direction.toUnitVector()) ) : map.moveTo( position.subtract(direction.toUnitVector()) );
            position = newPosition;
            energy -= moveEnergy;
            positionChanged(oldPosition, newPosition);
        } else for (int i = 0; i < moveID; i++) direction = direction.next();

    }

    // Reproduces two animals if they are healthy
    public Animal reproduce(Animal mom) {
        if (energy > startEnergy * 0.5 && mom.getEnergy() > startEnergy * 0.5) {
            Genotype childGenotype = genotype.getChildGenotype(mom.getGenotype(), (double) energy / (energy + mom.getEnergy()));
            int childEnergy = giveOutEnergy() + mom.giveOutEnergy();
            return new Animal(map, position, childEnergy, moveEnergy, childGenotype);
        }
        return null;
    }

    // Used when the child is born to give it out 25% of the energy
    private int giveOutEnergy() {
        int energyToGive = (int) (energy * 0.25);
        energy -= energyToGive;
        return energyToGive;
    }

    // Animal is dead when its energy is smaller than moveEnergy because we move before we eat
    public boolean isDead() {
        return energy < moveEnergy;
    }

    public boolean isAt(Vector2d position) {
        return (this.position.equals(position));
    }

    @Override
    public String toString() {
        return direction.toString();
    }

    /* --- Getters Section --- */
    public Vector2d getPosition(){
        return this.position;
    }

    public MapDirection getDirection(){
        return this.direction;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    public int getEnergy() {
        return energy;
    }

    /* --- Observers Section --- */
    public void addObserver(IPositionChangeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPos, Vector2d newPos) {
        for(IPositionChangeObserver observer: observers) {
            observer.positionChanged(this, oldPos, newPos);
        }
    }

}