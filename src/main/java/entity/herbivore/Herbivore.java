package entity.herbivore;

import entity.Animal;
import entity.Plant;
import island.Cell;
import settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Herbivore extends Animal {
    public static final List<Class<? extends Animal>> HERBIVORE_CLASSES = Arrays.asList(Boar.class, Buffalo.class, Caterpillar.class, Deer.class, Duck.class, Goat.class, Horse.class, Mouse.class, Rabbit.class, Sheep.class);

    @Override
    public void eat(Cell cell) {
        if (this.getFoodNeededForMaxSatiety() > 0) {
            if (this.getConsumptionProbability().size() > 1) {
                // eat для всеядных
                List<Animal> potentialPreys = new ArrayList<>();

                for (Animal animal : cell.getAnimals()) {
                    if (animal.isAvailable() && this.getConsumptionProbability().get(animal.getClass().getSimpleName().toLowerCase()) != null && this.getConsumptionProbability().get(animal.getClass().getSimpleName().toLowerCase()) > 0) {
                        potentialPreys.add(animal);
                    }
                }

                if (potentialPreys.isEmpty()) {
                    eatPlants(cell);
                    return;
                }

                // Попытка охоты
                int randomTargetIndex = (int) (Math.random() * potentialPreys.size());

                Animal potentialPrey = potentialPreys.get(randomTargetIndex);

                double huntSuccessProbability = this.getConsumptionProbability().get(potentialPrey.getClass().getSimpleName().toLowerCase());

                // Если охота удалась
                if (Math.random() < huntSuccessProbability) {
                    double satietyIncrease = potentialPrey.getWeight() / this.getFoodNeededForMaxSatiety();
                    double newSatiety = this.getCurrentSatiety() + satietyIncrease;
                    this.setCurrentSatiety(Math.min(newSatiety, Settings.MAX_SATIETY));

                    // Делаем жертву охоты недоступной и добавляем в список умерших животных для последующего удаления
                    potentialPrey.setAvailable(false);
                    cell.getAnimalsDeadToday().add(potentialPrey);
                    cell.decrementAnimalsCount(potentialPrey);
                } else {
                    eatPlants(cell);
                }

            } else {
                // eat для исключительно травоядных
                if (this.getConsumptionProbability().size() == 1) {
                    eatPlants(cell);
                }
            }
        }
    }

    private void eatPlants(Cell cell) {
        int randomTargetIndex = (int) (Math.random() * cell.getPlants().size());
        Plant potentialPrey = cell.getPlants().get(randomTargetIndex);

        double weightNeededForMaxSatiety = (Settings.MAX_SATIETY - this.getCurrentSatiety()) * this.getFoodNeededForMaxSatiety();

        if (potentialPrey.getWeight() > weightNeededForMaxSatiety) {
            this.setCurrentSatiety(Settings.MAX_SATIETY);
            potentialPrey.reduceWeight(weightNeededForMaxSatiety);
        } else {
            double satietyIncrease = potentialPrey.getWeight() / this.getFoodNeededForMaxSatiety();
            double newSatiety = this.getCurrentSatiety() + satietyIncrease;
            this.setCurrentSatiety(newSatiety);
            potentialPrey.reduceWeight(Plant.PLANT_MAX_WEIGHT_PER_UNIT);
        }
    }
}
