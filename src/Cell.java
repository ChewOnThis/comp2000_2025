// Cell: one tile in the grid, with a biome and terrain. Handles its own drawing.
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Cell extends Rectangle {
    public static int SIZE = 35;
    public final int col, row;
    private Terrain terrain;
    private Biome biome;

    public Cell(int col, int row, int worldX, int worldY, Terrain terrain, Biome biome) {
        super(worldX, worldY, SIZE, SIZE);
        this.col = col;
        this.row = row;
        this.terrain = terrain;
        this.biome = biome;
    }

    public Terrain getTerrain() { return terrain; }
    public Biome getBiome() { return biome; }
    // Passability: water biome is walkable in this game; otherwise use terrain setting.
    public boolean passable() {
        if (biome != null && "Water".equalsIgnoreCase(biome.name())) return true;
        return terrain.passable;
    }
    public void setTerrain(Terrain t) { this.terrain = t; }
    public void setBiome(Biome b) { this.biome = b; }

    public void paint(Graphics g, Point mouse) {
        Color c = biome != null ? biome.color() : terrain.color;
        g.setColor(c);
        g.fillRect(x, y, width, height);

        int rr = (int)(c.getRed() * 0.6);
        int gg = (int)(c.getGreen() * 0.6);
        int bb = (int)(c.getBlue() * 0.6);
        g.setColor(new Color(rr, gg, bb, 120));
        g.drawRect(x, y, width, height);
    }
}
   