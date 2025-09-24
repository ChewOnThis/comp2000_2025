import java.awt.Color;
import java.awt.Graphics;

public class DroppedItem implements Renderable {
    public final int col, row;
    public final Item item;

    public DroppedItem(int col, int row, Item item) {
        this.col = col; this.row = row; this.item = item;
    }

   
    public DroppedItem(Item item, int col, int row) { this(col, row, item); }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        int cellX = col * Cell.SIZE + offsetX, cellY = row * Cell.SIZE + offsetY;
        int size = (int)(Cell.SIZE * 0.6);
        int x = cellX + (Cell.SIZE - size) / 2;
        int y = cellY + (Cell.SIZE - size) / 2;
        if ("Speed Powerup".equals(item.getName())) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, size, size);
            g.setColor(Color.RED);
            g.drawOval(x, y, size, size);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillOval(x, y, size, size);
        }
        g.setColor(Color.WHITE);
        String name = item.getName();
        int w = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + size / 2 - w / 2, y - 4);
    }
}
