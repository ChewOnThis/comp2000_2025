import java.awt.Color;
import java.util.Map;

public class GrasslandBiome implements Biome {
    public String name() { return "Grassland"; }
    public Terrain baseTerrain() { return Terrain.GRASS; }
    public Color color() { return new Color(92, 181, 82); }
    public Map<String, Integer> enemyWeights() { return Map.of("Slime", 70, "Wolf", 30); }
}
