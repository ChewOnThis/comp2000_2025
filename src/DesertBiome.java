import java.awt.Color;
import java.util.Map;

public class DesertBiome implements Biome {
    public String name() { return "Desert"; }
    public Terrain baseTerrain() { return Terrain.SAND; }
    public Color color() { return new Color(232, 216, 162); }
    public Map<String, Integer> enemyWeights() { return Map.of("Scorpion", 80, "Slime", 20); }
}
