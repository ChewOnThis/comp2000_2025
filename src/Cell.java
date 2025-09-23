import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Cell {
    public static final int SIZE = 24;
    private final int col, row;
    private Terrain terrain = Terrain.GRASS;

    public Cell(int col, int row) { this.col = col; this.row = row; }
    public int colIndex() { return col; }
    public int rowIndex() { return row; }

    public void setTerrain(Terrain t) { this.terrain = t; }
    public Terrain getTerrain() { return terrain; }

    public void paint(Graphics g, Point mouse, int offsetX, int offsetY) {
        int x = col * SIZE + offsetX;
        int y = row * SIZE + offsetY;
        g.setColor(terrain.getColour());
        g.fillRect(x, y, SIZE, SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, SIZE, SIZE);
    }
}
