package settings;

import lombok.Getter;

import java.util.Map;

public class ConsumptionProbabilityTable {

    private ConsumptionProbabilityTable() {}

    // Поле для хранения всей таблицы шанса потребления
    @Getter
    private static Map<String, Map<String, Map<String, Double>>> consumptionProbability;

    public static void setConsumptionProbability(Map<String, Map<String, Map<String, Double>>> consumptionProbability) {
        ConsumptionProbabilityTable.consumptionProbability = consumptionProbability;
    }
}
