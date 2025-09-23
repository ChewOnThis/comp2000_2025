import java.awt.Graphics;
import java.awt.Point;

public class Grid {
    public final int columns, rows;
    public final Cell[][] cells;

    public Grid(int columns, int rows, int seed) {
        this.columns = columns; this.rows = rows;
        cells = new Cell[columns][rows];
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                cells[c][r] = new Cell(c, r);
            }
        }
    }

    public boolean inBounds(int c, int r) { return c >= 0 && r >= 0 && c < columns && r < rows; }
    public Cell cellAt(int c, int r) { return inBounds(c, r) ? cells[c][r] : null; }

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
}
