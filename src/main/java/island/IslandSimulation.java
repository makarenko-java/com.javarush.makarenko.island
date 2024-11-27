package island;

import entity.Animal;
import settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        for (int day = 1; day <= days; day++) {

            System.out.println("День " + day);
            System.out.println(collectAnimalStatistics());

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

            // рост растений
        }
        executorService.shutdown();  // Остановка ExecutorService
    }

    // Сбор статистики о количестве животных по всем клеткам
    private Map<String, Integer> collectAnimalStatistics() {
        Map<String, Integer> animalStatistics = new HashMap<>();

        for (int i = 0; i < island.getFieldRows(); i++) {
            for (int j = 0; j < island.getFieldColumns(); j++) {

                Cell cell = island.getCell(i, j);

                for (Animal animal : cell.getAnimals()) {

                    String animalClassName = animal.getClass().getSimpleName().toLowerCase();

                    if (animalStatistics.containsKey(animalClassName)) {
                        animalStatistics.put(animalClassName, animalStatistics.get(animalClassName) + 1);
                    } else {
                        animalStatistics.put(animalClassName, 1);
                    }
                }
            }
        }
        return animalStatistics;
    }
}
