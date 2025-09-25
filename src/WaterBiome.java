// Water biome: watery regions; piranhas only.
import java.awt.Color;
import java.util.Map;

public class WaterBiome implements Biome {
    @Override public String name() { return "Water"; }
    @Override public Terrain baseTerrain() { return Terrain.WATER; }
    @Override public Color color() { return new Color(80, 146, 218); }
    @Override public Map<String, Integer> enemyWeights() { return Map.of("Piranha", 100); }
}
