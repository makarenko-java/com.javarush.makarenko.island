package entity.predator;

import entity.Animal;
import island.Cell;
import settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Predator extends Animal {
    public static final List<Class<? extends Animal>> PREDATOR_CLASSES = Arrays.asList(Bear.class, Boa.class, Eagle.class, Fox.class, Wolf.class);

    @Override
    public void eat(Cell cell) {
        if (this.getFoodNeededForMaxSatiety() > 0) {
            List<Animal> potentialPreys = new ArrayList<>();

            for (Animal animal : cell.getAnimals()) {
                if (animal.isAvailable() && this.getConsumptionProbability().get(animal.getClass().getSimpleName().toLowerCase()) > 0) {
                    potentialPreys.add(animal);
                }
            }

            if (potentialPreys.isEmpty()) return;

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
            }
        }
    }

    @Override
    public void move() {

    }

    @Override
    public void chooseDirection() {

    }
}
