package entity.herbivore;

import island.Cell;

public class Caterpillar extends Herbivore {

    public Caterpillar() {
        super();
    }

    @Override
    public void reproduce(Cell cell) {
        // Гусеницы не умеют размножаться
    }
}
