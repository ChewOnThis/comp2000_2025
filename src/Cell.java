import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

// The Cell class represents a single tile in the game grid.
// Each cell has a terrain type and an optional biome, which affects its color and passability.
public class Cell extends Rectangle {
    // The size (width/height in pixels) of each cell. Static so all cells share the same size.
    public static int SIZE = 35;

    // The column and row indices of this cell in the grid.
    public final int col, row;

    // The terrain type of this cell (e.g., grass, sand, water, forest).
    private Terrain terrain;

    // The biome of this cell (e.g., Grassland, Desert, Water, Forest).
    private Biome biome;

    // Constructor: initializes the cell's position, terrain, and biome.
    public Cell(int col, int row, int worldX, int worldY, Terrain terrain, Biome biome) {
        // Rectangle stores the pixel position and size of the cell.
        super(worldX, worldY, SIZE, SIZE);
        this.col = col;
        this.row = row;
        this.terrain = terrain;
        this.biome = biome;
    }

    // Returns the terrain type of this cell.
    public Terrain getTerrain() { return terrain; }

    // Returns the biome of this cell.
    public Biome getBiome() { return biome; }

    // Determines if the cell is passable by the player.
    // Water biome is always passable, otherwise depends on terrain.
    public boolean passable() {
        if (biome != null && "Water".equalsIgnoreCase(biome.name())) {
            return true;
        }
        return terrain.passable;
    }

    // Sets the terrain type of this cell.
    public void setTerrain(Terrain t) { this.terrain = t; }

    // Sets the biome of this cell.
    public void setBiome(Biome b) { this.biome = b; }

    // Renders the cell on the screen.
    // Fills the cell with its biome color, then draws a darker, translucent border.
    public void paint(Graphics g, Point mouse) {
        // Determine the fill color: biome color if present, otherwise terrain color.
        Color c = biome != null ? biome.color() : terrain.color;
        g.setColor(c);
        g.fillRect(x, y, width, height);

        // Calculate a darker, slightly transparent color for the grid border.
        int rr = (int)(c.getRed() * 0.6);
        int gg = (int)(c.getGreen() * 0.6);
        int bb = (int)(c.getBlue() * 0.6);
        Color gridColor = new Color(rr, gg, bb, 120); // 120 alpha for transparency

        // Draw the border rectangle.
        g.setColor(gridColor);
        g.drawRect(x, y, width, height);
    }
}
