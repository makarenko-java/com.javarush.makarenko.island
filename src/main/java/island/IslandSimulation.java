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
            System.out.println("День " + day);

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

            // Добавление и удаление животных
            addToAnimalsList();
            //
            decreaseAnimalsSatiety();
            //
            removeFromAnimalsList();

            // Вывод статистики по острову в конце дня
            System.out.println(collectEntityStatistics());
        }
        executorService.shutdown();  // Остановка ExecutorService
    }

    // Сбор статистики о количестве животных по всем клеткам
    private String collectEntityStatistics() {
        Map<String, Integer> entitylStatistics = new LinkedHashMap<>();
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
            }
        }

        String returnStatistics = "Состояние на конец дня, Animal: " + entitylStatistics + ", суммарный вес растений: " + totalPlantWeight + ".";
        return returnStatistics;
    }

    private void addToAnimalsList() {

    }

    private void removeFromAnimalsList() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i,j);
                cell.getAnimals().removeAll(cell.getAnimalsDeadToday());
            }
        }
    }

    private void decreaseAnimalsSatiety() {
        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {
                Cell cell = island.getCell(i,j);

                for (Animal animal : cell.getAnimals()) {
                    animal.decreaseSatiety();
                    if (animal.getCurrentSatiety() <= 0) {
                        cell.getAnimalsDeadToday().add(animal);                    }
                }
            }
        }
    }
}
