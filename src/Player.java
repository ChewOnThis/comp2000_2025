import java.awt.Color;
import java.awt.Graphics;


public class Player extends Actor {
    public Player(Cell inLoc) {
        loc = inLoc;
        color = Color.RED;
    }

    @Override
    public void paint(Graphics g, int offsetX, int offsetY) {
        int cx = loc.x + offsetX + Cell.size / 2;
        int cy = loc.y + offsetY + Cell.size / 2;
        int r = 30;
        g.setColor(color);
        g.fillOval(cx - r / 2, cy - r / 2, r, r);
        g.setColor(Color.GRAY);
        g.drawOval(cx - r / 2, cy - r / 2, r, r);
    }
}
  