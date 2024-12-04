package settings;

public class Settings {

    private Settings() {}

    // Коэффициент масштабируемости количества животных для заселения клетки
    // 1.0 - максимальное число животных в клетке равно параметру max_per_cell
    // 0.1 - максимальное число животных в клетке равно 0.1 от параметра max_per_cell
    public static final double ANIMAL_POPULATION_SCALE = 1.0;

    // Максимальное количество потоков в пуле для ExecutorService
    public static final int THREAD_POOL_SIZE = 8;
    // Максимальное количество потоков в пуле для ScheduledExecutorService
    public static final int SCHEDULED_THREAD_POOL_SIZE = 1;


    // Размеры острова
    public static final int ISLAND_ROWS = 10;
    public static final int ISLAND_COLUMNS = 10;

    // Количество дней симуляции
    public static final int SIMULATION_DAYS = 100;

    // Параметры класса Plant
    public static final double PLANT_MAX_WEIGHT_PER_UNIT = 1.0;
    public static final int MAX_PLANTS_PER_CELL = 200;
    public static final double PLANT_GROWTH_PER_CYCLE = 0.25;

    // Уменьшение текущей сытости у животных в день
    public static final double DAILY_SATIETY_DECREASE = 0.1;
    // Максимальный уровень сытости
    public static final double MAX_SATIETY = 1.0;


    // Параметры животных
    // Вероятность удачного размножения
    public static final double REPRODUCTION_PROBABILITY = 0.1;
    // Размер помёта
    public static final int LITTER_SIZE = 1;
    // Коэффициент полового дисбаланса (при = 0.5, < 0.5 - только самки, > 0.5 - только самцы)
    public static final double BIRTH_GENDER_RATIO = 0.5;
    // Коэффициент уровня сытости для принятия решения о перемещении
    public static final double SATIETY_LEVEL_TO_MOVE = 0.75;
}
