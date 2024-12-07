package island;

import entity.Animal;
import entity.Plant;
import entity.herbivore.Herbivore;
import entity.predator.Predator;
import settings.Settings;

import java.util.*;
import java.util.concurrent.*;

public class IslandSimulation {

    // Поля
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Island island;

    // Конструкторы
    public IslandSimulation(Island island) {
        this.island = island;
        this.executorService = Executors.newFixedThreadPool(Settings.THREAD_POOL_SIZE);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(Settings.SCHEDULED_THREAD_POOL_SIZE);
    }

    // Методы
    public void startSimulation(int days) {

        System.out.printf("Создан остров размером %dx%d.\n", island.getFieldRows(), island.getFieldColumns());

        for (int day = 1; day <= days; day++) {
            // Вывод номера дня
            System.out.println("День " + day + ". ");
            // Запуск работы животных в клетках
            processDay();
            // Добавление рожденных и переместившихся в текущую клетку животных в список животных клетки
            addToAnimalsList();
            // Уменьшение сытости всех животных с проверкой на то, не уменьшилась ли сытость до нуля или ниже
            decreaseAnimalsSatiety();
            // Удаление животных, которые умерли и переместились из клетки, из списка животных клетки
            removeFromAnimalsList();
            // Рост растений
            growPlants();
            // Вывод статистики по острову в конце дня
            System.out.println(collectEntityStatistics());

        }
        executorService.shutdown();  // Остановка ExecutorService
    }

    public void startSimulationWithScheduledExecutorService(int days) {

        System.out.printf("Создан остров размером %dx%d.\n", island.getFieldRows(), island.getFieldColumns());

        // Запускаем периодический рост растений в ScheduledExecutorService
        scheduledExecutorService.scheduleWithFixedDelay(this::growPlants, 1000, 50, TimeUnit.MILLISECONDS);


        for (int day = 1; day <= days; day++) {
            // Вывод номера дня
            System.out.println("День " + day + ". ");
            // Запуск работы животных в клетках
            processDay();
            // Добавление рожденных и переместившихся в текущую клетку животных в список животных клетки
            addToAnimalsList();
            // Уменьшение сытости всех животных с проверкой на то, не уменьшилась ли сытость до нуля или ниже
            decreaseAnimalsSatiety();
            // Удаление животных, которые умерли и переместились из клетки, из списка животных клетки
            removeFromAnimalsList();
            // Вывод статистики по острову в конце дня
            System.out.println(collectEntityStatistics());

        }
        executorService.shutdown();  // Остановка ExecutorService
        scheduledExecutorService.shutdown(); // Остановка ScheduledExecutorService
    }

    private void processDay() {
        // --------------------------------------------------------------------------------
        // Основная реализация многопоточности через execute()

        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                executorService.execute(island.getCell(i, j));
            }
        }

        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);
                if (!cell.isRunCompleted()) j--;
                else cell.setRunCompleted(false);
            }
        }
        // --------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------
        // Альтернативная реализация многопоточности через submit() и объекты Future
//
//            List<Future<?>> futures = new ArrayList<>();
//            for (int i = 0; i < island.getFieldRows(); i++) {
//                for (int j = 0; j < island.getFieldColumns(); j++) {
//                    Future<?> future = executorService.submit(island.getCell(i, j));
//                    futures.add(future);
//                }
//            }
//
//            // Ожидаем завершения обработки всех клеток
//            for (Future<?> future : futures) {
//                try {
//                    future.get();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        // --------------------------------------------------------------------------------
    }

    private String collectEntityStatistics() {
        Map<String, Integer> entitylStatistics = new LinkedHashMap<>();
        int totalAnimalCount = 0;
        double totalPlantWeight = 0.0;

        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);

                for (Animal animal : cell.getAnimals()) {
                    String animalClassName = animal.getClass().getSimpleName();

                    if (entitylStatistics.containsKey(animalClassName)) {
                        entitylStatistics.put(animalClassName, entitylStatistics.get(animalClassName) + 1);
                    } else {
                        entitylStatistics.put(animalClassName, 1);
                    }
                }

                for (Plant plant : cell.getPlants()) {
                    totalPlantWeight += plant.getWeight();
                }

                totalAnimalCount += cell.getAnimals().size();
            }
        }

        String returnStatistics;
        StringBuilder outputText = new StringBuilder();

        // Если есть животные - делаем вывод по количеству животных. Иначе делаем упрощенный вывод.
        if (totalAnimalCount != 0) {
            outputText.append("Состояние по количеству животных на конец дня: ");
            for (Class<? extends Animal> herbivoreClass : Herbivore.HERBIVORE_CLASSES) {
                String className = herbivoreClass.getSimpleName();
                String emoji = Herbivore.HERBIVORE_EMOJI.get(Herbivore.HERBIVORE_CLASSES.indexOf(herbivoreClass));
                if (entitylStatistics.containsKey(className)) {
                    outputText.append(emoji + " " + entitylStatistics.get(className) + ", ");
                }
            }

            for (Class<? extends Animal> predatorClass : Predator.PREDATOR_CLASSES) {
                String className = predatorClass.getSimpleName();
                String emoji = Predator.PREDATOR_EMOJI.get(Predator.PREDATOR_CLASSES.indexOf(predatorClass));
                if (entitylStatistics.containsKey(className)) {
                    outputText.append(emoji + " " + entitylStatistics.get(className) + ", ");
                }
            }

            outputText.append(String.format("\b\b. Суммарный вес растений: %s %.2f.", Plant.PLANT_EMOJI, totalPlantWeight));
        } else {
            outputText.append("Состояние на конец дня. Все животные умерли.");
        }

        returnStatistics = outputText.toString();

//        // Код для проверки соответствия суммы всех животных поля-счетчика животных по классам
//        // и суммарного числа животных в поле животных. Они должны быть равны.
//        int totalAnimalCountFromAnimalsCountByClass = 0;
//
//        for (int i = 0; i < island.getFieldRows(); i++) {
//            for (int j = 0; j < island.getFieldColumns(); j++) {
//                Cell cell = island.getCell(i, j);
//
//                for (Map.Entry<Class<? extends Animal>, Integer> animalClassCount : cell.getAnimalsCountByClass().entrySet()) {
//                    totalAnimalCountFromAnimalsCountByClass += animalClassCount.getValue();
//                }
//            }
//        }

        return returnStatistics;
    }

    private void addToAnimalsList() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);
                cell.getAnimals().addAll(cell.getAnimalsBornToday());
                cell.getAnimals().addAll(cell.getAnimalsMovedInToday());
                cell.getAnimalsBornToday().clear();
                cell.getAnimalsMovedInToday().clear();
            }
        }
    }

    private void removeFromAnimalsList() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);
                cell.getAnimals().removeAll(cell.getAnimalsDeadToday());
            }
        }
    }

    private void decreaseAnimalsSatiety() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);

                for (Animal animal : cell.getAnimals()) {
                    animal.decreaseSatiety();
                    if (animal.getCurrentSatiety() <= 0) {
                        cell.getAnimalsDeadToday().add(animal);
                        cell.decrementAnimalsCount(animal);
                    }
                }
            }
        }
    }

    private void growPlants() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);

                for (Plant plant : cell.getPlants()) {
                    plant.grow();
                }
            }
        }
    }
}
