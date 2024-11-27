package island;

public class Island {

    private final int fieldRows;

    private final int fieldColumns;

    private final Cell[][] field;

    public Island(int rows, int columns) {
        this.fieldRows = rows;
        this.fieldColumns = columns;
        this.field = new Cell[fieldRows][fieldColumns];
        initializeField();
    }

    private void initializeField() {
        for (int i = 0; i < fieldRows; i++) {
            for (int j = 0; j < fieldColumns; j++) {
                field[i][j] = new Cell();
            }
        }
    }

    public int getFieldColumns() {
        return fieldColumns;
    }

    public int getFieldRows() {
        return fieldRows;
    }

    public Cell getCell(int row, int column) {
        if (row >= 0 && row < fieldRows && column >= 0 && column < fieldColumns) {
            return field[row][column];
        }
        throw new IndexOutOfBoundsException("Недопустимые координаты ячейки");
    }
}
