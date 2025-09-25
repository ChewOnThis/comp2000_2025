// Grassland biome: balanced terrain; slimes common, some wolves.
import java.awt.Color;
import java.util.Map;

public class GrasslandBiome implements Biome {
    @Override public String name() { return "Grassland"; }
    @Override public Terrain baseTerrain() { return Terrain.GRASS; }
    @Override public Color color() { return new Color(92, 181, 82); }
    @Override public Map<String, Integer> enemyWeights() { return Map.of("Slime", 70, "Wolf", 30); }
}
