import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Cell {
    public static int SIZE = 24; // was final; now adjustable at runtime
    private final int col, row;
    private Terrain terrain = Terrain.GRASS;
    private CellEffect effect = CellEffect.NONE;
    private Biome biome = null; // new: biome reference


    public Cell(int col, int row) { this.col = col; this.row = row; }
    public int colIndex() { return col; }
    public int rowIndex() { return row; }

    public void setTerrain(Terrain t) { this.terrain = t; }
    public Terrain getTerrain() { return terrain; }

    // new: biome accessors
    public void setBiome(Biome b) { this.biome = b; }
    public Biome getBiome() { return biome; }

    public void setEffect(CellEffect e) { this.effect = e; }
    public CellEffect getEffect() { return effect; }

    // consider biome rules (water biome is passable) before terrain passable
    public boolean passable() {
        if (biome != null && "Water".equalsIgnoreCase(biome.name())) return true;
        return terrain.passable;
    }

    public void paint(Graphics g, Point mouse, int offsetX, int offsetY) {
        int x = col * SIZE + offsetX;
        int y = row * SIZE + offsetY;
        g.setColor(terrain.color);
        g.fillRect(x, y, SIZE, SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, SIZE, SIZE);
    }
}

