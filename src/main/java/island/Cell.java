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

    // Константы
    @Getter
    public static final List<Class<? extends Animal>> ANIMAL_CLASSES = new ArrayList<>();

    static {
        ANIMAL_CLASSES.addAll(Herbivore.HERBIVORE_CLASSES);
        ANIMAL_CLASSES.addAll(Predator.PREDATOR_CLASSES);
    }

    // Поля
    @Getter
    private final int coordinateX;  // Координата клетки по оси X на острове
    @Getter
    private final int coordinateY;      // Координата клетки по оси Y на острове

    private boolean isRunCompleted = false; // Флаг завершения работы метода run()

    @Getter
    private final List<Animal> animals = new ArrayList<>(); // Список животных в клетке
    @Getter
    private final List<Plant> plants = new ArrayList<>();   // Список растений в клетке

    @Getter
    private final List<Animal> animalsBornToday = new ArrayList<>();  // Родившиеся животные
    @Getter
    private final List<Animal> animalsDeadToday = new ArrayList<>();  // Погибшие животные

    private final List<Animal> animalsMovedInToday = new ArrayList<>();  // Прибывшие животные
    @Getter
    private final List<Animal> animalsMovedOutToday = new ArrayList<>();  // Убывшие животные

    // Список животных, доступных для размножения (которые не участвовали в размножении в текущий день)
    @Getter
    private final List<Animal> partnersForReproduce = new ArrayList<>();

    // Счетчик животных в клетке по классам для учета
    private final Map<Class<? extends Animal>, Integer> animalsCountByClass = new HashMap<>();

    // Блокировка для синхронизации доступа к списку прибывших животных
    private final Lock animalsMovedInLock = new ReentrantLock();
    // Блокировка для синхронизации доступа к счетчику
    private final Lock animalsCountByClassLock = new ReentrantLock();
    // Блокировка для синхронизации доступа к флагу завернешния
    private final Lock runCompletedLock = new ReentrantLock();

    // Конструкторы
    public Cell(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.populateAnimals();
        this.populatePlants();
    }

    // Методы
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

    public List<Animal> getAnimalsMovedInToday() {
        animalsMovedInLock.lock();
        try {
            return animalsMovedInToday;
        } finally {
            animalsMovedInLock.unlock();
        }
    }

    public void addAnimalsMovedInToday(Animal animal) {
        animalsMovedInLock.lock();
        try {
            animalsMovedInToday.add(animal);
        } finally {
            animalsMovedInLock.unlock();
        }
    }

    public boolean isRunCompleted() {
        runCompletedLock.lock();
        try {
            return isRunCompleted;
        } finally {
            runCompletedLock.unlock();
        }
    }

    public void setRunCompleted(boolean isRunCompleted) {
        runCompletedLock.lock();
        try {
            this.isRunCompleted = isRunCompleted;
        } finally {
            runCompletedLock.unlock();
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
            // Животное перемещается
            animal.move();
        }

        // Очищаем список животных, доступных для размножения
        partnersForReproduce.clear();

//        // Вывод того сколько родилось в каждой клетке за день
//        System.out.println("Родилось за день: " + animalsBornToday.size());

        // Устанавливаем флаг, что метод run завершил работу
        setRunCompleted(true);
    }
}
