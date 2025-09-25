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
        // Use biome color if present; otherwise terrain color
        Color base = biome != null ? biome.color() : terrain.color;
        g.setColor(base);
        g.fillRect(x, y, SIZE, SIZE);

        // Subtle darker, translucent border to make grid more readable
        int rr = (int)(base.getRed() * 0.6);
        int gg = (int)(base.getGreen() * 0.6);
        int bb = (int)(base.getBlue() * 0.6);
        g.setColor(new Color(rr, gg, bb, 120));
        g.drawRect(x, y, SIZE, SIZE);
    }
}

