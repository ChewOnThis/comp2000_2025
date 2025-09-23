import java.util.*;

public class EnemyFactory {
    private final Random rng;
    public EnemyFactory(int seed) { rng = new Random(seed * 31L + 17L); }

    public Enemy create(String name, int c, int r) {
        return switch (name) {
            case "Slime" -> new SlimeEnemy(c, r);
            case "Wolf" -> new WolfEnemy(c, r);
            case "Scorpion" -> new ScorpionEnemy(c, r);
            case "Piranha" -> new PiranhaEnemy(c, r);
            default -> new SlimeEnemy(c, r);
        };
    }
}
