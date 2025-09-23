import java.awt.Color;
import java.util.Map;

public class ForestBiome implements Biome {
    public String name() { return "Forest"; }
    public Terrain baseTerrain() { return Terrain.GRASS; }
    public Color color() { return new Color(34, 139, 34); }
    public Map<String, Integer> enemyWeights() { return Map.of("Wolf", 2, "Slime", 1); }
}
