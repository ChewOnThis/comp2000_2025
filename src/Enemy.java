import java.awt.Color;
import java.awt.Graphics;

public abstract class Enemy implements Actor {
    protected int col, row;
    protected Color color = Color.YELLOW;
    protected int hp = 1;

    public Enemy(int c, int r) { this.col = c; this.row = r; }
    
    @Override public int col() { return col; }
    @Override public int row() { return row; }
    @Override public void setPosition(int c, int r) { this.col = c; this.row = r; }
    @Override public boolean isDead() { return hp <= 0; }
    @Override public void damage(int d) { hp -= d; }
    @Override public void update(double dt) { }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
          
           int size = (int)(Cell.SIZE * 0.8);
          int x = col * Cell.SIZE + (Cell.SIZE - size) / 2 + offsetX;
        int y = row * Cell.SIZE + (Cell.SIZE - size) / 2 + offsetY;
        g.setColor(color);
         g.fillRect(x, y, size, size);
      g.setColor(Color.BLACK);
    g.drawRect(x, y, size, size);

        
        g.setColor(Color.WHITE);
        String name = type();
        int w = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + size / 2 - w / 2, y - 4);
    }
     public abstract String type();
}
