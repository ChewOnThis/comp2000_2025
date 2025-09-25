// Forest biome: dense greenery; wolves common, some slimes.
import java.awt.Color;
import java.util.Map;

public class ForestBiome implements Biome {
    @Override public String name() { return "Forest"; }
    @Override public Terrain baseTerrain() { return Terrain.FOREST; }
    @Override public Color color() { return new Color(34, 139, 34); }
    @Override public Map<String, Integer> enemyWeights() { return Map.of("Wolf", 60, "Slime", 40); }
}
