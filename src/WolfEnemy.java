import java.awt.Color;
public class WolfEnemy extends Enemy {
    public WolfEnemy(int c, int r) { super(c, r); this.color = Color.CYAN; }
    @Override public String type() { return "Wolf"; }
}