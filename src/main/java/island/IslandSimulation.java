package island;

import entity.Animal;
import entity.Plant;
import settings.Settings;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IslandSimulation {

    private final ExecutorService executorService;
    private final Island island;

    public IslandSimulation(Island island) {
        this.island = island;
        this.executorService = Executors.newFixedThreadPool(Settings.THREAD_POOL_SIZE); // Количество потоков
    }

    public void startSimulation(int days) {

        System.out.printf("Создан остров размером %dx%d.\n", island.getFieldRows(), island.getFieldColumns());
        for (int day = 1; day <= days; day++) {
            // Вывод номера дня
            System.out.println("День " + day + ". ");

            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < island.getFieldRows(); i++) {
                for (int j = 0; j < island.getFieldColumns(); j++) {
                    Future<?> future = executorService.submit(island.getCell(i, j));
                    futures.add(future);
                }
            }

            // Ожидаем завершения обработки всех клеток
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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

    // Сбор статистики о количестве животных по всем клеткам
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
//
//        // Вывод для проверки счетчиков
//        String returnStatistics = "Состояние на конец дня, Animal, всего: " + totalAnimalCountFromAnimalsCountByClass + "(" + totalAnimalCount + "), количество животных по классам - " + entitylStatistics + ", суммарный вес растений: " + totalPlantWeight + ".";

        String returnStatistics = "Состояние на конец дня, Animal, всего: " + totalAnimalCount + ", количество животных по классам: " + entitylStatistics + ". Суммарный вес растений: " + totalPlantWeight + ".";
        return returnStatistics;
    }

    private void addToAnimalsList() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i, j);
                cell.getAnimals().addAll(cell.getAnimalsBornToday());
                cell.getAnimalsBornToday().clear();
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
