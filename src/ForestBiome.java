import java.awt.Color;
import java.util.Map;

public class ForestBiome implements Biome {
    public String name() { return "Forest"; }
    public Terrain baseTerrain() { return Terrain.FOREST; }
    public Color color() { return new Color(34, 139, 34); }
    public Map<String, Integer> enemyWeights() { return Map.of("Wolf", 60, "Slime", 40); }
}
