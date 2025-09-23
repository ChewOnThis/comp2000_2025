import java.awt.Color;
import java.util.Map;

public interface Biome {
    String name();
    Terrain baseTerrain();
    Color color();
    Map<String, Integer> enemyWeights();
}
