import java.awt.Color;
import java.awt.Graphics;

public class DroppedItem implements Renderable {
    // The item being dropped.
    public final Item item;
    // The column and row position of the drop.
    public int col, row;

    // Constructor: initializes the dropped item and its position.
    public DroppedItem(int col, int row, Item item) {
        this.col = col; this.row = row; this.item = item;
    }

    // Alternative constructor that accepts parameters in a different order.
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
            g.setColor(Color.ORANGE);
            g.fillOval(x, y, size, size);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, size, size);
        }
        g.setColor(Color.WHITE);
        String name = item.getName();
        int w = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + size / 2 - w / 2, y - 4);
    }
}

