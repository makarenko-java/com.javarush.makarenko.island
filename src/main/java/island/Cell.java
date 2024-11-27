package island;

import entity.Animal;
import entity.herbivore.Herbivore;
import entity.predator.Predator;
import settings.AnimalCharacteristicsTable;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class Cell implements Runnable {
    public Cell() {
        this.populateAnimals();
    }

    public static final List<Class<? extends Animal>> ANIMAL_CLASSES = new ArrayList<>();

    static {
        ANIMAL_CLASSES.addAll(Herbivore.HERBIVORE_CLASSES);
        ANIMAL_CLASSES.addAll(Predator.PREDATOR_CLASSES);
    }

    private List<Animal> animals = new ArrayList<>();

    public void populateAnimals() {
        for (Class<? extends Animal> animalClass : ANIMAL_CLASSES) {
            String className = animalClass.getSimpleName().toLowerCase();
            int maxPerCell = (int) AnimalCharacteristicsTable.getAnimalCharacteristics().get(className).get("max_per_cell");
            // Случайное количество особей для каждого животного в конкретной клетке,
            // с учетом коэффициента масштабируемости
            int populationCount = (int) (Settings.ANIMAL_POPULATION_SCALE * (Math.random() * (maxPerCell + 1)));

            for (int i = 0; i < populationCount; i++) {
                try {
                    animals.add(animalClass.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Не удалось создать объект для " + className);
                }
            }
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    @Override
    public void run() {
        for (Animal animal : animals) {
            animal.eat();
            animal.reproduce();
            animal.chooseDirection();
            animal.move();
        }
    }
}
