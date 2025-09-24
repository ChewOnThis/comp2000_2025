import java.util.*;

public class EnemyFactory {
    private final Random rng;
    public EnemyFactory(int seed) { this.rng = new Random(seed * 31L + 17L); }

    public Enemy create(String name, int c, int r) {
        return switch (name) {
            case "Slime" -> new SlimeEnemy(c, r);
            case "Wolf" -> new WolfEnemy(c, r);
            case "Scorpion" -> new ScorpionEnemy(c, r);
            case "Piranha" -> new PiranhaEnemy(c, r);
            default -> new SlimeEnemy(c, r);
        };
    }
      
    public Enemy create(String name, int c, int r, String biomeName) { return create(name, c, r); }

    
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
