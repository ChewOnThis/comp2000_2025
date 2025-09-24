import java.awt.Color;
import java.awt.Graphics;

public class Player implements Actor {
    private int col, row;
    private int hp = 10;
    private final Color color = Color.RED;
    private final Inventory<Item> inventory = new Inventory<>();
    public Player(int col, int row) {
        this.col = col;
        this.row = row;
    }
    @Override
    public int col() { return col; }
    @Override
    public int row() { return row; }
    @Override
    public void setPosition(int c, int r) { this.col = c; this.row = r; }
    @Override
    public boolean isDead() { return hp <= 0; }
    @Override
    public void damage(int d) { hp -= d; }
    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
     
        int cx = col * Cell.SIZE + Cell.SIZE/2 + offsetX;
        int cy = row * Cell.SIZE + Cell.SIZE/2 + offsetY;
        int radius = (int)(Cell.SIZE * 0.8); 
        g.setColor(color);
        g.fillOval(cx - radius/2, cy - radius/2, radius, radius);
        g.setColor(Color.BLACK);
        g.drawOval(cx - radius/2, cy - radius/2, radius, radius);
    }


    @Override
    public void update(double dt) {}
    public Inventory<Item> inventory() { return inventory; }
}
