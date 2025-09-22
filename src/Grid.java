import java.awt.Graphics;
import java.awt.Point;
import java.util.Optional;

public class Grid {
  public int columns;
  public int rows;
  Cell[][] cells;

  public Grid(int columns, int rows) {
    this.columns = columns;
    this.rows = rows;
    cells = new Cell[columns][rows];
    for(int i=0; i<columns; i++) {
      for(int j=0; j<rows; j++) {
        cells[i][j] = new Cell(colToLabel(i), j, 10 + Cell.size * i, 10 + Cell.size * j);
      }
    }
  }

  private char colToLabel(int col) {
    return (char) (col + Character.valueOf('A'));
  }

  private int labelToCol(char col) {
    return (int) (col - Character.valueOf('A'));
  }

  public void paint(Graphics g, Point mousePos, int offsetX, int offsetY) {
    for(int i=0; i<columns; i++) {
      for(int j=0; j<rows; j++) {
        Cell cell = cells[i][j];
        g.translate(offsetX, offsetY);
        cell.paint(g, mousePos);
        g.translate(-offsetX, -offsetY);
      }
    }
  }

  public Optional<Cell> cellAtColRow(int c, int r) {
    if(c >= 0 && c < cells.length && r >=0 && r < cells[c].length) {
      return Optional.of(cells[c][r]);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Cell> cellAtColRow(char c, int r) {
    return cellAtColRow(labelToCol(c), r);
  }

  public Optional<Cell> cellAtPoint(Point p) {
    for(int i=0; i < cells.length; i++) {
      for(int j=0; j < cells[i].length; j++) {
        if(cells[i][j].contains(p)) {
          return Optional.of(cells[i][j]);
        }
      }
    }
    return Optional.empty();
  }
}

