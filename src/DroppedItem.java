import java.awt.Color;
import java.awt.Graphics;

public class DroppedItem implements Renderable {
    public final int col, row;
    public final Item item;

    public DroppedItem(int col, int row, Item item) {
        this.col = col; this.row = row; this.item = item;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        int x = col * Cell.SIZE + offsetX, y = row * Cell.SIZE + offsetY;
        g.setColor(Color.MAGENTA);
        g.fillOval(x + 6, y + 6, Cell.SIZE - 12, Cell.SIZE - 12);
        g.setColor(Color.BLACK);
        g.drawString(item.getName(), x + 2, y + Cell.SIZE - 4);
    }
}
