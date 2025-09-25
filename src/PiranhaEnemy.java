// PiranhaEnemy: water biome fish.
import java.awt.Color;
public class PiranhaEnemy extends Enemy {
    public PiranhaEnemy(int c, int r) { super(c, r); this.color = Color.GREEN; }
    @Override public String type() { return "Piranha"; }
}