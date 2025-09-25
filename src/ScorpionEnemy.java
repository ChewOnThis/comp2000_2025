// ScorpionEnemy: desert-dwelling stinger.
import java.awt.Color;
public class ScorpionEnemy extends Enemy {
    public ScorpionEnemy(int c, int r) { super(c, r); this.color = Color.MAGENTA; }
    @Override public String type() { return "Scorpion"; }
}