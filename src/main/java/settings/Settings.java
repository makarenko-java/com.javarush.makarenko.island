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
    public static final int SIMULATION_DAYS = 200;

    // Параметры класса Plant
    public static final double PLANT_MAX_WEIGHT_PER_UNIT = 1.0;
    public static final int MAX_PLANTS_PER_CELL = 200;
    public static final double PLANT_GROWTH_PER_CYCLE = 0.25;

    // Уменьшение текущей сытости у животных в день, процентов
    public static final double DAILY_SATIETY_DECREASE = 0.05;
    // Максимальный уровень сытости в процентах
    public static final double MAX_SATIETY = 1.0;


    // Параметры размножения животных
    // Вероятность удачного размножения
    public static final double REPRODUCTION_PROBABILITY = 0.1;
    // Размер помёта
    public static final int LITTER_SIZE = 1;
    // Коэффициент вероятности рождения самцов (< 0.5 - только самки, > 0.5 - только самцы)
    public static final double BIRTH_GENDER_RATIO = 0.5;
}
