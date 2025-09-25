// SlimeEnemy: common enemy found in grassland.
import java.awt.Color;
public class SlimeEnemy extends Enemy {
    public SlimeEnemy(int c, int r) { super(c, r); this.color = Color.PINK; }
    @Override public String type() { return "Slime"; }
}