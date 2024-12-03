package island;

import lombok.Getter;
import settings.Settings;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Island {

    // Константы
    private static final String INVALID_COORDINATES_MESSAGE = "Недопустимые координаты ячейки";

    // Статические поля
    private static Island instance;
    private static final Lock islandLock = new ReentrantLock();

    // Поля
    @Getter
    private final int fieldRows;
    @Getter
    private final int fieldColumns;
    private final Cell[][] field;

    // Конструкторы
    private Island(int rows, int columns) {
        this.fieldRows = rows;
        this.fieldColumns = columns;
        this.field = new Cell[fieldRows][fieldColumns];
        initializeField();
    }

    // Статические методы
    public static Island getInstance() {
        islandLock.lock(); // Блокировка всего метода
        try {
            if (instance == null) {
                if (Settings.ISLAND_ROWS >= 1 && Settings.ISLAND_COLUMNS >= 1) {
                    instance = new Island(Settings.ISLAND_ROWS, Settings.ISLAND_COLUMNS);
                } else {
                    instance = new Island(1, 1);
                }
            }
            return instance;
        } finally {
            islandLock.unlock(); // Разблокировка в конце
        }
    }

    // Методы
    private void initializeField() {
        for (int i = 0; i < fieldRows; i++) {
            for (int j = 0; j < fieldColumns; j++) {
                field[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell getCell(int row, int column) {
        if (row >= 0 && row < fieldRows && column >= 0 && column < fieldColumns) {
            return field[row][column];
        }
        throw new IndexOutOfBoundsException("INVALID_COORDINATES_MESSAGE");
    }
}