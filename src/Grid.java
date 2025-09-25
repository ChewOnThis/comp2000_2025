import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

public class Grid {
    public final int columns, rows;
    public final Cell[][] cells;
    private final Noise noiseHigh;
    private final Random rng;
    private final List<Biome> biomes = List.of(
        new GrasslandBiome(), new DesertBiome(), new WaterBiome(), new ForestBiome()
    );

    public Grid(int columns, int rows, int seed) {
        this.columns = columns; this.rows = rows;
        this.noiseHigh = new Noise(seed ^ 0x5bd1e995);
        this.rng = new Random(seed);
        cells = new Cell[columns][rows];
        generate();
    }

    private void generate() {
        int k = Math.max(6, (columns * rows) / 500);
        Point[] centres = new Point[k];
        Biome[] centreBiome = new Biome[k];
        for (int i = 0; i < k; i++) {
            centres[i] = new Point(rng.nextInt(columns), rng.nextInt(rows));
            centreBiome[i] = biomes.get(rng.nextInt(biomes.size()));
        }

        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                int best = 0;
                double bestD2 = Double.POSITIVE_INFINITY;
                for (int i = 0; i < k; i++) {
                    int dx = c - centres[i].x, dy = r - centres[i].y;
                    double d2 = dx * dx + dy * dy;
                    if (d2 < bestD2) { bestD2 = d2; best = i; }
                }
                cells[c][r] = new Cell(c, r);
                // assign biome and its base terrain
                Biome b = centreBiome[best];
                cells[c][r].setBiome(b);
                cells[c][r].setTerrain(b.baseTerrain());
            }
        }

        for (int c = 1; c < columns - 1; c++) {
            for (int r = 1; r < rows - 1; r++) {
                double v = noiseHigh.value(c * 3, r * 3);
                if (v > 0.62) {
                    cells[c][r].setTerrain(Terrain.WATER);
                    // make noisy water patches belong to the water biome for correct spawns
                    cells[c][r].setBiome(new WaterBiome());
                }
            }
        }
    }

    public boolean inBounds(int c, int r) { return c >= 0 && r >= 0 && c < columns && r < rows; }
    public Cell cellAt(int c, int r) { return inBounds(c, r) ? cells[c][r] : null; }

     public Optional<Cell> cellAtOpt(int c, int r) {
        return inBounds(c, r) ? Optional.of(cells[c][r]) : Optional.empty();
    }
    public void paint(Graphics g, Point mouse, int offsetX, int offsetY, int viewCols, int viewRows, int focusCol, int focusRow) {
        int startC = Math.max(0, focusCol - viewCols / 2 - 1);
        int endC   = Math.min(columns, focusCol + viewCols / 2 + 2);
        int startR = Math.max(0, focusRow - viewRows / 2 - 1);
        int endR   = Math.min(rows, focusRow + viewRows / 2 + 2);
        for (int c = startC; c < endC; c++) {
            for (int r = startR; r < endR; r++) {
                cells[c][r].paint(g, mouse, offsetX, offsetY);
            }
        }
    }

    public boolean passable(int c, int r) {
        return inBounds(c, r) && cells[c][r].passable();
    }
}
