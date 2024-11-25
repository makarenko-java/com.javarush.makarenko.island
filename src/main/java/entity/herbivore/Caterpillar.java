package entity.herbivore;

import settings.AnimalCharacteristicsTable;
import settings.ConsumptionProbabilityTable;

import java.util.Map;

public class Caterpillar extends Herbivore {
    private Map<String, Object> animalCharacteristicsTable = AnimalCharacteristicsTable.getAnimalCharacteristics().get(this.getClass().getSimpleName().toLowerCase());
    private double weight = (double) animalCharacteristicsTable.get("weight");
    private int max_per_cell = (int) animalCharacteristicsTable.get("max_per_cell");
    private int max_speed = (int) animalCharacteristicsTable.get("max_speed");
    private double food_needed_for_max_satiety = (double) animalCharacteristicsTable.get("food_needed_for_max_satiety");

    private Map<String, Double> consumptionProbability = ConsumptionProbabilityTable.getConsumptionProbability()
            .get(super.getClass().getSuperclass().getSimpleName().toLowerCase())
            .get(this.getClass().getSimpleName().toLowerCase());

    public double getWeight() {
        return weight;
    }

    public int getMax_per_cell() {
        return max_per_cell;
    }

    public int getMax_speed() {
        return max_speed;
    }

    public double getFood_needed_for_max_satiety() {
        return food_needed_for_max_satiety;
    }

    public Map<String, Double> getConsumptionProbability() {
        return consumptionProbability;
    }
}
