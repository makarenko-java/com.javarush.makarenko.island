package island;

import entity.Animal;
import entity.Plant;
import entity.herbivore.Herbivore;
import entity.predator.Predator;
import lombok.Getter;
import settings.AnimalCharacteristicsTable;
import settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell implements Runnable {

    @Getter
    public static final List<Class<? extends Animal>> ANIMAL_CLASSES = new ArrayList<>();

    static {
        ANIMAL_CLASSES.addAll(Herbivore.HERBIVORE_CLASSES);
        ANIMAL_CLASSES.addAll(Predator.PREDATOR_CLASSES);
    }

    // Список животных в клетке
    @Getter
    private final List<Animal> animals = new ArrayList<>();
    // Список растений в клетке
    @Getter
    private final List<Plant> plants = new ArrayList<>();

    // Списки животных, которые родились, были съедены, прибыли в клетку и убыли из клетки
    @Getter
    private final List<Animal> animalsBornToday = new ArrayList<>();
    @Getter
    private final List<Animal> animalsDeadToday = new ArrayList<>();

    private final List<Animal> animalsMovedInToday = new ArrayList<>();
    @Getter
    private final List<Animal> animalsMovedOutToday = new ArrayList<>();

    // Список животных, доступных для размножения (которые не участвовали в размножении в текущий день)
    @Getter
    private final List<Animal> partnersForReproduce = new ArrayList<>();


    // Карта счетчик животных в клетке по классам для учета maxPerCell при использовании методов reproduce() и move()
    private final Map<Class<? extends Animal>, Integer> animalsCountByClass = new HashMap<>();


    // ReentrantLock для синхронизации доступа к спискам
    private final Lock animalsMovedInLock = new ReentrantLock();
    private final Lock animalsCountByClassLock = new ReentrantLock();



    public Cell() {
        this.populateAnimals();
        this.populatePlants();
    }



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

    public void populatePlants() {
        for (int i = 0; i < Plant.MAX_PLANTS_PER_CELL; i++) {
            plants.add(new Plant());
        }
    }


    public void calculateAnimalsCount() {
        animalsCountByClassLock.lock();
        try {
            animalsCountByClass.clear();

            for (Animal animal : animals) {
                Class<? extends Animal> animalClass = animal.getClass();
                if (animalsCountByClass.containsKey(animalClass)) {
                    int count = animalsCountByClass.get(animalClass);
                    animalsCountByClass.put(animalClass, count + 1);
                } else {
                    animalsCountByClass.put(animalClass, 1);
                }
            }
        } finally {
            animalsCountByClassLock.unlock();
        }
    }

    public Map<Class<? extends Animal>, Integer> getAnimalsCountByClass() {
        animalsCountByClassLock.lock();
        try {
            return animalsCountByClass;
        } finally {
            animalsCountByClassLock.unlock();
        }
    }

    public boolean incrementAnimalsCount(Animal animal) {
        animalsCountByClassLock.lock();
        try {
            Class<? extends Animal> animalClass = animal.getClass();
            if (animalsCountByClass.containsKey(animalClass)) {

                int count = animalsCountByClass.get(animalClass);

                if (animal.getMaxPerCell() >= (count + 1)) {
                    animalsCountByClass.put(animalClass, count + 1);
                    return true;
                } else {
                    return false;
                }
            } else {
                animalsCountByClass.put(animalClass, 1);
                return true;
            }
        } finally {
            animalsCountByClassLock.unlock();
        }
    }

    public void decrementAnimalsCount(Animal animal) {
        animalsCountByClassLock.lock();
        try {
            Class<? extends Animal> animalClass = animal.getClass();
            if (animalsCountByClass.containsKey(animalClass)) {
                int count = animalsCountByClass.get(animalClass);
                if (count > 1) {
                    animalsCountByClass.put(animalClass, count - 1);
                } else {
                    animalsCountByClass.remove(animalClass);
                }
            }
        } finally {
            animalsCountByClassLock.unlock();
        }
    }

    @Override
    public void run() {
        // Заполняем Map-счетчик количества животных по классам
        this.calculateAnimalsCount();
        // Заполняем список животных, доступных для размножения
        this.partnersForReproduce.addAll(this.getAnimals());

        for (Animal animal : animals) {
            // Проверка животного на доступность, isAvailable = false, если,
            // - животное только что родилось
            // - животное было съедено
            // - животное покинуло клетку
            // - животное переместилось в эту клетку из другой
            if (!animal.isAvailable()) continue;

            // Животное кушает
            animal.eat(this);

            // Животное размножается
            animal.reproduce(this);

//            animal.chooseDirection(); // Выбирает направление
//            animal.move();      // Двигается
        }

        // Очищаем список животных, доступных для размножения
        partnersForReproduce.clear();
//        // Вывод того сколько родилось в каждой клетке за день
//        System.out.println("Родилось за день: " + animalsBornToday.size());
    }
}
