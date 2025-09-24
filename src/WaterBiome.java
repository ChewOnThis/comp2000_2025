import java.awt.Color;
import java.util.Map;

public class WaterBiome implements Biome {
    public String name() { return "Water"; }
    public Terrain baseTerrain() { return Terrain.WATER; }
    public Color color() { return new Color(80, 146, 218); }
    public Map<String, Integer> enemyWeights() { return Map.of("Piranha", 100); }
}
