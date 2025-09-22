import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class Stage {
    int columns, rows;
    Grid grid;
    List<Actor> actors;
    Player player;

    public Stage() {
        columns = 10;
        rows = 10;  
        grid = new Grid(columns, rows);
        actors = new ArrayList<Actor>();
        int centerCol = columns / 2;
        int centerRow = rows / 2;
        player = new Player(grid.cellAtColRow(centerCol, centerRow).get());
        actors.add(player);
    }

    public void movePlayer(int dx, int dy) {
        int colIndex = player.loc.col - 'A';
        int newColIndex = colIndex + dx;
        int newRow = player.loc.row + dy;
        if (newColIndex >= 0 && newColIndex < columns && newRow >= 0 && newRow < rows) {
            player.loc = grid.cellAtColRow(newColIndex, newRow).get();
        }
    }

    public void paint(Graphics g, Point mouseLoc) {
        int windowWidth = 1024;
        int windowHeight = 720;
        int offsetX = (windowWidth / 2) - player.loc.x - (Cell.size / 2);
        int offsetY = (windowHeight / 2) - player.loc.y - (Cell.size / 2);

        grid.paint(g, mouseLoc, offsetX, offsetY);
        for (Actor a : actors) {
            a.paint(g, offsetX, offsetY);
        }
        if (mouseLoc != null) {
            grid.cellAtPoint(mouseLoc).ifPresent(hoverCell -> {
                g.setColor(Color.DARK_GRAY);
                g.drawString(hoverCell.col + String.valueOf(hoverCell.row), 740, 30);
            });
        }
    }
}