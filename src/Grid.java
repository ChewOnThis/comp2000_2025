import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

public class Grid {
    public final int cols, rows;
    public final Cell[][] cells;
    private final List<Biome> biomeChoices = List.of(
        new GrasslandBiome(), new DesertBiome(), new WaterBiome(), new ForestBiome()
    );
    private final Random rng;
    private final Noise noiseHigh;
    private final int cellSize = Cell.SIZE;
    private List<Point> centers;
    private Map<Integer, Biome> centerBiome;

    public Grid(int cols, int rows, int seed) {
        this.cols = cols; this.rows = rows;
        this.rng = new Random(seed);
        this.noiseHigh = new Noise(seed ^ 0x5bd1e995);
        this.cells = new Cell[cols][rows];
        generate();
    }

    public void regenerate(int newSeed) {
        for (int c=0;c<cols;c++) for (int r=0;r<rows;r++) cells[c][r]=null;
        centers = null; centerBiome = null;
        Grid g2 = new Grid(cols, rows, newSeed);
        for (int c=0;c<cols;c++) {
            System.arraycopy(g2.cells[c], 0, this.cells[c], 0, rows);
        }
        this.centers = g2.centers; this.centerBiome = g2.centerBiome;
    }

    private void generate() {
        int nCenters = Math.max(4, (cols*rows) / 800);
        nCenters = Math.min(nCenters, cols * rows);
        if (nCenters <= 0) nCenters = 4;
        centers = new ArrayList<>();
        centerBiome = new HashMap<>();
        for (int i=0;i<nCenters;i++) {
            int c = rng.nextInt(Math.max(1, cols)), r = rng.nextInt(Math.max(1, rows));
            centers.add(new Point(c, r));
            Biome b = biomeChoices.get(rng.nextInt(biomeChoices.size()));
            centerBiome.put(i, b);
        }

        int[][] owner = new int[cols][rows];
        int[][] second = new int[cols][rows];
        for (int c=0;c<cols;c++) for (int r=0;r<rows;r++) {
            double best = Double.MAX_VALUE, snd = Double.MAX_VALUE; int bi=-1, si=-1;
            for (int i=0;i<centers.size();i++) {
                Point p = centers.get(i);
                double d = dist2(c, r, p.x, p.y);
                if (d < best) { snd = best; si = bi; best = d; bi = i; }
                else if (d < snd) { snd = d; si = i; }
            }
            owner[c][r] = bi; second[c][r] = si;
        }

        for (int c=0;c<cols;c++) for (int r=0;r<rows;r++) {
            Biome b = centerBiome.get(owner[c][r]);
            cells[c][r] = new Cell(c, r, c*cellSize, r*cellSize, b.baseTerrain(), b);
        }

        for (int c=0;c<cols;c++) for (int r=0;r<rows;r++) {
            int o = owner[c][r];
            boolean near = false;
            for (int dc=-1; dc<=1 && !near; dc++) for (int dr=-1; dr<=1; dr++) {
                if (dc==0&&dr==0) continue;
                int nc=c+dc, nr=r+dr; if (inBounds(nc,nr) && owner[nc][nr]!=o) { near=true; break; }
            }
            if (!near) continue;
            double v = noiseHigh.value(c*3, r*3);
            if (v > 0.58) {
                int s = second[c][r];
                Biome b2 = centerBiome.get(s);
                cells[c][r].setBiome(b2);
                cells[c][r].setTerrain(b2.baseTerrain());
            }
        }
    }

    private boolean inBounds(int c,int r){return c>=0&&r>=0&&c<cols&&r<rows;}
    private double dist2(int ax,int ay,int bx,int by){int dx=ax-bx,dy=ay-by; return dx*dx+dy*dy;}

    public Optional<Cell> cellAt(int c,int r){
        if(!inBounds(c,r)) return Optional.empty();
        return Optional.of(cells[c][r]);
    }

    public void paint(Graphics g, Point mouse, int offsetX, int offsetY, int viewCols, int viewRows, int focusCol, int focusRow) {
        int startC = Math.max(0, focusCol - viewCols/2 - 1);
        int endC   = Math.min(cols, focusCol + viewCols/2 + 2);
        int startR = Math.max(0, focusRow - viewRows/2 - 1);
        int endR   = Math.min(rows, focusRow + viewRows/2 + 2);
        for (int c=startC;c<endC;c++) for (int r=startR;r<endR;r++) {
            Cell cell = cells[c][r];
            g.translate(offsetX, offsetY);
            cell.paint(g, mouse);
            g.translate(-offsetX, -offsetY);
        }
    }
}
