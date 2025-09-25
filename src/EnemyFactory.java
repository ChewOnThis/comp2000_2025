import java.util.Map;
import java.util.Random;

public class EnemyFactory {
    // Random number generator for enemy selection.
    private final Random rng;
    
    // Constructor: initializes RNG with a seed.
    public EnemyFactory(long seed) { 
        this.rng = new Random(seed ^ 0x9E3779B97F4A7C15L); 
    }

    // Creates an enemy of the given type at the specified position.
    public Enemy create(String type, int c, int r) {
        return switch (type) {
            case "Slime" -> new SlimeEnemy(c, r);
            case "Wolf" -> new WolfEnemy(c, r);
            case "Scorpion" -> new ScorpionEnemy(c, r);
            case "Piranha" -> new PiranhaEnemy(c, r);
            default -> new SlimeEnemy(c, r);
        };
    }
    
    // Creates an enemy with biome information (for compatibility with srcFINAL).
    public Enemy create(String type, int c, int r, String biomeName) {
        return create(type, c, r); // biomeName is ignored for now
    }

    // Picks a random enemy type based on weighted probabilities.
    public String pickWeighted(Map<String, Integer> weights) {
        int sum = 0;
        for (int w : weights.values()) sum += w;
        int roll = rng.nextInt(Math.max(sum, 1));
        int acc = 0;
        for (var e : weights.entrySet()) {
            acc += e.getValue();
            if (roll < acc) return e.getKey();
        }
        return weights.keySet().iterator().next();
    }
}
