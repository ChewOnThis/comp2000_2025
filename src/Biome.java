// Biome: high-level environment theme for cells (name, base terrain, color, and enemy mix).
// Implementations: GrasslandBiome, DesertBiome, WaterBiome, ForestBiome.
import java.awt.Color;
import java.util.Map;

public interface Biome {
    // Display name used in UI.
    String name();
    // Default terrain material for cells in this biome.
    Terrain baseTerrain();
    // Color tint to paint the cells.
    Color color();
    // Enemy type weights for spawning (higher = more likely).
    Map<String, Integer> enemyWeights();
}
