import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Cell {
    public static final int SIZE = 24;
    private final int col, row;
    private Terrain terrain = Terrain.GRASS;
    private CellEffect effect = CellEffect.NONE;


    public Cell(int col, int row) { this.col = col; this.row = row; }
    public int colIndex() { return col; }
    public int rowIndex() { return row; }

    public void setTerrain(Terrain t) { this.terrain = t; }
    public Terrain getTerrain() { return terrain; }
    public void setEffect(CellEffect e) { this.effect = e; }
    public CellEffect getEffect() { return effect; }
    public boolean passable() { return terrain.passable; }

    public void paint(Graphics g, Point mouse, int offsetX, int offsetY) {
        int x = col * SIZE + offsetX;
        int y = row * SIZE + offsetY;
        g.setColor(terrain.getColor());
      
        g.fillRect(x, y, SIZE, SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, SIZE, SIZE);
    }
}
