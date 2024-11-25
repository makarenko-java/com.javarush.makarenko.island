package settings;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class TableInitializer {

    private TableInitializer() {}

    // Инициализация данных таблиц
    public static void initialize() {
        loadConsumptionProbabilityTable();
        loadAnimalCharacteristicsTable();
    }

    // Загрузка данных из consumption_probability_table.yaml
    private static void loadConsumptionProbabilityTable() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = TableInitializer.class.getResourceAsStream("/consumption_probability_table.yaml")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Файл не найден: consumption_probability_table.yaml");
            }
            // Загрузка данных из YAML в Map
            Map<String, Map<String, Map<String, Double>>> data = yaml.loadAs(inputStream, Map.class);
            // Сохранение данных в ConsumptionProbabilityTable
            ConsumptionProbabilityTable.setConsumptionProbability(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке данных из YAML файла", e);
        }
    }

    // Загрузка данных из animal_characteristics_table.yaml
    private static void loadAnimalCharacteristicsTable() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = TableInitializer.class.getResourceAsStream("/animal_characteristics_table.yaml")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Файл не найден: animal_characteristics_table.yaml");
            }
            // Загрузка данных из YAML в Map
            Map<String, Map<String, Object>> data = yaml.loadAs(inputStream, Map.class);
            // Сохранение данных в AnimalCharacteristicsTable
            AnimalCharacteristicsTable.setAnimalCharacteristics(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке данных из YAML файла", e);
        }
    }
}