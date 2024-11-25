package entity.herbivore;

import entity.Animal;

import java.util.Arrays;
import java.util.List;

public abstract class Herbivore extends Animal {
    public static final List<Class<? extends Animal>> HERBIVORE_CLASSES = Arrays.asList(
            Boar.class, Buffalo.class, Caterpillar.class, Deer.class, Duck.class, Goat.class, Horse.class, Mouse.class, Rabbit.class, Sheep.class);

    @Override
    public void eat() {

    }

    @Override
    public void reproduce() {

    }

    @Override
    public void move() {

    }

    @Override
    public void chooseDirection() {

    }
}
