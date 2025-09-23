import java.awt.Color;
import java.util.Map;

public class DesertBiome implements Biome {
    public String name() { return "Desert"; }
    public Terrain baseTerrain() { return Terrain.SAND; }
    public Color color() { return new Color(218, 180, 90); }
    public Map<String, Integer> enemyWeights() { return Map.of("Scorpion", 3); }
}
