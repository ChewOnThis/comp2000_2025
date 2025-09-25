// Desert biome: sandy terrain, scorpions common.
import java.awt.Color;
import java.util.Map;

public class DesertBiome implements Biome {
    @Override public String name() { return "Desert"; }
    @Override public Terrain baseTerrain() { return Terrain.SAND; }
    @Override public Color color() { return new Color(232, 216, 162); }
    @Override public Map<String, Integer> enemyWeights() { return Map.of("Scorpion", 80, "Slime", 20); }
}
