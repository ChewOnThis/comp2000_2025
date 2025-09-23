import java.awt.Color;
import java.awt.Graphics;

public class Dog implements Actor {
    private int c, r; private int hp = 1;
    public Dog(Cell start) { this.c = start.colIndex(); this.r = start.rowIndex(); }
    @Override public int col() { return c; }
    @Override public void update(Grid grid) { /* idle */ }
    @Override public int row() { return r; }
    @Override public void setPosition(int c, int r) { this.c = c; this.r = r; }
    @Override public boolean isDead() { return hp <= 0; }
    @Override public void damage(int d) { hp -= d; }
    @Override public void render(Graphics g, int offsetX, int offsetY) {
        
        int x = c * Cell.SIZE + offsetX, y = r * Cell.SIZE + offsetY;
        g.setColor(Color.ORANGE);
        g.fillRect(x + 4, y + 4, Cell.SIZE - 8, Cell.SIZE - 8);
        g.setColor(Color.BLACK);
        g.drawString("Dog", x + 4, y + Cell.SIZE - 6);
    }
}
