import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Stage {
    private final Grid grid;
    private final Player player;
    private final List<Actor> actors = new ArrayList<>();
    private final int viewCols = 30, viewRows = 22;
    private boolean started = false;
    private boolean showInventory = false;

    public Stage(int cols, int rows, int seed) {
        this.grid = new Grid(cols, rows, seed);
        this.player = new Player(cols / 2, rows / 2);
        actors.add(player);
        actors.add(new Dog(grid.cellAt(2, 2)));
        actors.add(new Cat(grid.cellAt(5, 7)));
        actors.add(new Bird(grid.cellAt(10, 10)));
    }

    public void startGame() { started = true; }
    public void toggleInventory() { showInventory = !showInventory; }

    public void movePlayer(int dc, int dr) {
        int nc = player.col() + dc;
        int nr = player.row() + dr;
        if (grid.inBounds(nc, nr)) player.setPosition(nc, nr);
    }

    public void paint(Graphics g, Point mouse, int ww, int wh) {
        if (!started) {
            g.setColor(Color.BLACK);
            g.drawString("Press Enter to start. WASD/Arrows to move. I for inventory.", 20, 30);
            return;
        }
        int offsetX = ww / 2 - player.col() * Cell.SIZE - Cell.SIZE / 2;
        int offsetY = wh / 2 - player.row() * Cell.SIZE - Cell.SIZE / 2;

        grid.paint(g, mouse, offsetX, offsetY, viewCols, viewRows, player.col(), player.row());
        for (Actor a : actors) a.render(g, offsetX, offsetY);

        if (showInventory) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(8, 8, 200, 120);
            g.setColor(Color.WHITE);
            g.drawString("Inventory", 16, 28);
            int y = 48;
            for (var e : player.inventory().snapshot().entrySet()) {
                g.drawString(e.getKey() + " x" + e.getValue(), 16, y);
                y += 16;
            }
        }
    }
}
