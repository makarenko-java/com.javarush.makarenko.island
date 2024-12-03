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
        // Для животного происходит поиск партнера соответствующего класса и пола.
        // Когда партнер для размножения находится - выполняется бросок кубика
        // и проверка через константу вероятности размножения в настройках.
        // И вне зависимости от удачности/не удачности размножения оба животных удаляются из списка
        // потенциальных партнеров для размножения, так как они уже поучаствовали в размножении в текущем дне.

        // Если животное находится в списке доступных к размножению партнеров,
        // т.е. животное ранее не участвовало в размножении с другим животным
        if (cell.getPartnersForReproduce().contains(this)) {
            // То перебирая каждое животное из этого списка ищем партнера
            for (Animal partnerForReproduce : cell.getPartnersForReproduce()) {
                // Если класс животного равен классу животного-потенциального партнера
                // и если пол животного не совпадает с полом животного-потенциального партнера, то
                if (this.getClass() == partnerForReproduce.getClass() && !this.getGender().equals(partnerForReproduce.getGender())) {
                    // Бросаем кубик и если бросок кубика меньше вероятности удачного размножения, то
                    if (Math.random() < Settings.REPRODUCTION_PROBABILITY) {
                        // Создаем новых животных в этой клетке, добавляя их в отдельный список новых животных,
                        // которые будут добавлены в основной список в конце дня
                        // при этом инкрементируем список-счетчик животных клетки
                        try {
                            for (int i = 0; i < Settings.LITTER_SIZE; i++) {
                                // Проверяем поле maxPerCell для класса животного
                                // и имеются ли свободные места в клетке где происходит размножение
                                // с помощью поля-счетчика Map<Class<? extends Animal>, Integer> animalsCountByClass
                                Animal newAnimal = this.getClass().getDeclaredConstructor().newInstance();
                                // cell.incrementAnimalsCount(newAnimal) возвращает boolean если есть свободные места
                                if (cell.incrementAnimalsCount(newAnimal)) {
                                    cell.getAnimalsBornToday().add(newAnimal);
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Ошибка при создании нового животного: " + e.getMessage(), e);
                        }
                    }
                    // Удаляем животных из списка доступных к размножению партнеров
                    cell.getPartnersForReproduce().remove(this);
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
