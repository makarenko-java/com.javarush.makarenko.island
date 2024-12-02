package entity;

import lombok.Getter;
import settings.Settings;

@Getter
public class Plant {
    public static final double PLANT_MAX_WEIGHT_PER_UNIT = Settings.PLANT_MAX_WEIGHT_PER_UNIT;
    public static final int MAX_PLANTS_PER_CELL = Settings.MAX_PLANTS_PER_CELL;
    public static final double PLANT_GROWTH_PER_CYCLE = Settings.PLANT_GROWTH_PER_CYCLE;

    private double weight;

    public Plant() {
        this.weight = PLANT_MAX_WEIGHT_PER_UNIT;
    }

    public void grow() {
        weight = Math.min(weight + PLANT_GROWTH_PER_CYCLE, PLANT_MAX_WEIGHT_PER_UNIT);
    }

    public void reduceWeight(double amount) {
        weight = Math.max(weight - amount, 0);
    }
}
