package settings;

public class Settings {

    private Settings() {}

    // Коэффициент масштабируемости количества животных для заселения клетки
    // 1.0 - максимальное число животных в клетке равно параметру max_per_cell
    // 0.1 - максимальное число животных в клетке равно 0.1 от параметра max_per_cell
    public static final double ANIMAL_POPULATION_SCALE = 1.0;

    // Максимальное количество потоков в пуле для обработки задач.
    public static final int THREAD_POOL_SIZE = 8;

    // Размеры острова
    public static final int ISLAND_ROWS = 10;
    public static final int ISLAND_COLUMNS = 10;

    // Количество дней симуляции
    public static final int SIMULATION_DAYS = 100;
}
