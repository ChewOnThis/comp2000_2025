public class EnemyFactory {
    public EnemyFactory(int seed) { /* currently stateless */ }

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
