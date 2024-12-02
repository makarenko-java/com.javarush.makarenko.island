package entity;

import island.Cell;
import lombok.Getter;
import lombok.Setter;
import settings.AnimalCharacteristicsTable;
import settings.ConsumptionProbabilityTable;
import settings.Settings;

import java.util.Map;

@Getter
public abstract class Animal {
    private final double weight;
    private final int maxPerCell;
    private final int maxSpeed;
    private final double foodNeededForMaxSatiety;
    private final Map<String, Double> consumptionProbability;
    @Setter
    private double currentSatiety;
    private final String gender;
    @Setter
    private boolean isAvailable;


    public Animal() {
        String className = this.getClass().getSimpleName().toLowerCase();
        String parentName = this.getClass().getSuperclass().getSimpleName().toLowerCase();

        Map<String, Object> characteristics = AnimalCharacteristicsTable.getAnimalCharacteristics().get(className);
        this.weight = (double) characteristics.get("weight");
        this.maxPerCell = (int) characteristics.get("max_per_cell");
        this.maxSpeed = (int) characteristics.get("max_speed");
        this.foodNeededForMaxSatiety = (double) characteristics.get("food_needed_for_max_satiety");
        this.consumptionProbability = ConsumptionProbabilityTable.getConsumptionProbability().get(parentName).get(className);
        this.currentSatiety = Settings.MAX_SATIETY;
        this.gender = (Math.random() > Settings.BIRTH_GENDER_RATIO) ? "male" : "female";
        this.isAvailable = true;
    }

    public abstract void eat(Cell cell);
    public abstract void move();
    public abstract void chooseDirection();


    public void reproduce(Cell cell) {
        // Проверка на то, участвовала ли самка в удачном размножении
        // Самка может участвовать только один раз за день, а самец много раз

        // Если животное самка, то
        if (this.getGender().equals("female")) {
            // Если самка находится в списке доступных к размножению партнеров, то
            if (cell.getPartnersForReproduce().contains(this)) {
                // То перебирая каждое животное из этого списка ищем ей партнера
                for (Animal partnerForReproduce : cell.getPartnersForReproduce()) {
                    // Если класс самки равен классу животного-потенциального партнера и если животное является самцом, то
                    if (this.getClass() == partnerForReproduce.getClass() && partnerForReproduce.getGender().equals("male")) {
                        // Если бросок кубика меньше вероятность удачного размножения, то
                        if (Math.random() < Settings.REPRODUCTION_PROBABILITY) {
                            // Создаем новых животных в этой клетке, добавляя их в отдельный список новых животных,
                            // которые будут добавлены в основной список в конце дня
                            try {
                                for (int i = 0; i < Settings.LITTER_SIZE; i++) {
                                    cell.getAnimalsBornToday().add(this.getClass().getDeclaredConstructor().newInstance());
                                }
                            } catch (Exception e) {
                                throw new RuntimeException("Ошибка при создании нового животного: " + e.getMessage(), e);
                            }
                        }
                        // Удаляем самку из списка доступных к размножению партнеров
                        cell.getPartnersForReproduce().remove(this);
                        // Прерываем цикл в случае нахождения партнера вне зависимости от удачности размножения
                        break;
                    }
                }
            }
        }

        // Если животное самец, то
        if (this.getGender().equals("male")) {
            // Иначе животное самец и перебирая каждое животное из списка доступных к размножению партнеров ищем ему партнера
            for (Animal partnerForReproduce : cell.getPartnersForReproduce()) {
                // Если класс самца равен классу животного-потенциального партнера и если животное является самкой, то
                if (this.getClass() == partnerForReproduce.getClass() && partnerForReproduce.getGender().equals("female")) {
                    // Если бросок кубика меньше вероятность удачного размножения, то
                    if (Math.random() < Settings.REPRODUCTION_PROBABILITY) {
                        // Создаем новых животных в этой клетке, добавляя их в отдельный список новых животных,
                        // которые будут добавлены в основной список в конце дня
                        try {
                            for (int i = 0; i < Settings.LITTER_SIZE; i++) {
                                cell.getAnimalsBornToday().add(this.getClass().getDeclaredConstructor().newInstance());
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Ошибка при создании нового животного: " + e.getMessage(), e);
                        }
                    }
                    // Удаляем самку из списка доступных к размножению партнеров
                    cell.getPartnersForReproduce().remove(partnerForReproduce);
                    // Прерываем цикл в случае нахождения партнера вне зависимости от удачности размножения
                    break;
                }
            }
        }
    }

    public void decreaseSatiety() {
        if (this.getFoodNeededForMaxSatiety() > 0) {
            this.setCurrentSatiety(this.getCurrentSatiety() - Settings.DAILY_SATIETY_DECREASE);
        }
    }
}
