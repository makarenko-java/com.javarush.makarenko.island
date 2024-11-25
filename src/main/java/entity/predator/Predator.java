package entity.predator;

import entity.Animal;

import java.util.Arrays;
import java.util.List;

public abstract class Predator extends Animal {
    public static final List<Class<? extends Animal>> PREDATOR_CLASSES = Arrays.asList(
            Bear.class, Boa.class, Eagle.class, Fox.class, Wolf.class);

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
