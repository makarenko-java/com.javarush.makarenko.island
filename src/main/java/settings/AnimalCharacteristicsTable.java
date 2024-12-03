package settings;

import lombok.Getter;

import java.util.Map;

public class AnimalCharacteristicsTable {

    private AnimalCharacteristicsTable() {}

    // Поле для хранения всей таблицы характеристик животных
    @Getter
    private static Map<String, Map<String, Object>> animalCharacteristics;

    public static void setAnimalCharacteristics(Map<String, Map<String, Object>> animalCharacteristics) {
        AnimalCharacteristicsTable.animalCharacteristics = animalCharacteristics;
    }
}
