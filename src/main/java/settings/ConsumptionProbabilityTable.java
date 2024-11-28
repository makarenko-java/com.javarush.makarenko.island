package settings;

import java.util.Map;

public class ConsumptionProbabilityTable {

    private ConsumptionProbabilityTable() {}

    // Поле для хранения всей таблицы шанса потребления
    private static Map<String, Map<String, Map<String, Double>>> consumptionProbability;

    public static Map<String, Map<String, Map<String, Double>>> getConsumptionProbability() {
        return consumptionProbability;
    }

    public static void setConsumptionProbability(Map<String, Map<String, Map<String, Double>>> consumptionProbability) {
        ConsumptionProbabilityTable.consumptionProbability = consumptionProbability;
    }
}
