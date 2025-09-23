import java.awt.Color;
import java.awt.Graphics;

public abstract class Enemy implements Actor {
    protected int col, row;
    protected Color color = Color.YELLOW;
    protected int hp = 1;

    public Enemy(int c, int r, Color color) { this.col = c; this.row = r; this.color = color; }

    @Override public int col() { return col; }
    @Override public int row() { return row; }
    @Override public void setPosition(int c, int r) { this.col = c; this.row = r; }
    @Override public boolean isDead() { return hp <= 0; }
    @Override public void damage(int d) { hp -= d; }
    @Override public void update(Grid grid) { }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        int x = col * Cell.SIZE + offsetX, y = row * Cell.SIZE + offsetY;
        g.setColor(color);
        g.fillRect(x + 4, y + 4, Cell.SIZE - 8, Cell.SIZE - 8);
        g.setColor(Color.BLACK);
        g.drawString(getClass().getSimpleName(), x + 2, y + Cell.SIZE - 4);
    }
}
