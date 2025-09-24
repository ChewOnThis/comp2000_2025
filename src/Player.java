import java.awt.Color;
import java.awt.Graphics;

public class Player implements Actor {
    private int col, row;
    private int hp = 10;
    private final Inventory<Item> inventory = new Inventory<>();

    public Player(int col, int row) { this.col = col; this.row = row; }
    public Inventory<Item> inventory() { return inventory; }

    @Override public int col() { return col; }
    @Override public int row() { return row; }
    @Override public void setPosition(int c, int r) { this.col = c; this.row = r; }
    @Override public boolean isDead() { return hp <= 0; }
    @Override public void damage(int d) { hp -= d; }
     @Override public void update(double dt) { }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        int x = col * Cell.SIZE + offsetX;
        int y = row * Cell.SIZE + offsetY;
        g.setColor(Color.RED);
        g.fillOval(x + 3, y + 3, Cell.SIZE - 6, Cell.SIZE - 6);
        g.setColor(Color.BLACK);
        g.drawString("HP:" + hp, x + 2, y + 12);
    }
}
