import island.Island;
import island.IslandSimulation;
import settings.Settings;
import settings.TableInitializer;

public class Main {
    public static void main(String[] args) {

        TableInitializer.initialize();

        Island island = Island.getInstance();

        IslandSimulation simulation = new IslandSimulation(island);

        // Симуляция при которой трава растет исключительно в цикле
        // simulation.startSimulation(Settings.SIMULATION_DAYS);

        // Симуляция при которой трава растет периодически в ScheduledExecutorService
        simulation.startSimulationWithScheduledExecutorService(Settings.SIMULATION_DAYS);
    }
}
