package island;

import lombok.Getter;
import settings.Settings;

public class Island {

    private static Island instance;
    @Getter
    private final int fieldRows;
    @Getter
    private final int fieldColumns;
    private final Cell[][] field;

    private Island(int rows, int columns) {
        this.fieldRows = rows;
        this.fieldColumns = columns;
        this.field = new Cell[fieldRows][fieldColumns];
        initializeField();
    }

    public static Island getInstance() {
        if (instance == null) {
            instance = new Island(Settings.ISLAND_ROWS, Settings.ISLAND_COLUMNS);
        }
        return instance;
    }

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
        throw new IndexOutOfBoundsException("Недопустимые координаты ячейки");
    }
}