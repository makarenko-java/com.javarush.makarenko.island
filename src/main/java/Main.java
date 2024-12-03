import island.Island;
import island.IslandSimulation;
import settings.Settings;
import settings.TableInitializer;

public class Main {
    public static void main(String[] args) {

        TableInitializer.initialize();

        Island island = Island.getInstance();

        IslandSimulation simulation = new IslandSimulation(island);

        simulation.startSimulation(Settings.SIMULATION_DAYS);
    }
}
