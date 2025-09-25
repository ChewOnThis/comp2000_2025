import java.util.*;

// EnemyFactory creates enemies based on type and biome.
// Uses a random number generator for weighted selection.
public class EnemyFactory {
    // Random number generator for enemy selection.
    private final Random rng;
    
    // Constructor: initializes RNG with a seed.
    public EnemyFactory(int seed) { 
        rng = new Random(seed * 31L + 17L); 
    }

    // Creates an enemy of the given type at the specified position.
    public Enemy create(String name, int c, int r) {
        return switch (name) {
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
