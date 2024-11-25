package settings;

import java.util.Map;

public class AnimalCharacteristicsTable {

    private AnimalCharacteristicsTable() {}

    // Поле для хранения всей таблицы характеристик животных
    private static Map<String, Map<String, Object>> animalCharacteristics;

    public static Map<String, Map<String, Object>> getAnimalCharacteristics() {
        return animalCharacteristics;
    }

    public static void setAnimalCharacteristics(Map<String, Map<String, Object>> animalCharacteristics) {
        AnimalCharacteristicsTable.animalCharacteristics = animalCharacteristics;
    }
}