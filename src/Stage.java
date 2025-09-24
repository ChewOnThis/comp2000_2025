import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Stage {
    private final Grid grid;
    private final Player player;
    private final List<Actor> actors = new ArrayList<>();
    private final int viewCols = 30, viewRows = 22;
    private boolean started = false;
    private boolean showInventory = false;
    private final EnemyFactory enemyFactory = new EnemyFactory(12345);
    private final List<DroppedItem> drops = new ArrayList<>();
    private final Random rng = new Random(0x9e3779b9);
    private final Inventory<Item> inventory = new Inventory<>();



    public Stage(int cols, int rows, int seed) {
        this.grid = new Grid(cols, rows, seed);
        this.player = new Player(cols / 2, rows / 2);
        actors.add(player);
        actors.add(new Dog(grid.cellAt(2, 2)));
        actors.add(new Cat(grid.cellAt(5, 7)));
        actors.add(new Bird(grid.cellAt(10, 10)));
        for (int i = 0; i < 24; i++) {
            int c = rng.nextInt(cols), r = rng.nextInt(rows);
            Terrain t = grid.cellAt(c, r).getTerrain();
            Enemy e = switch (t) {
                case GRASS -> new SlimeEnemy(c, r);
                case SAND  -> new ScorpionEnemy(c, r);
                case WATER -> new PiranhaEnemy(c, r);
                case FOREST -> new WolfEnemy(c, r);
            };
            actors.add(e);
        }

        for (int i = 0; i < 8; i++) {
            int c = rng.nextInt(cols), r = rng.nextInt(rows);
            drops.add(new DroppedItem((i % 3 == 0) ? new SpeedPowerup() : new Potion(), c, r));
        }

    }

    public void startGame() { started = true; }
    public void toggleInventory() { showInventory = !showInventory; }

    public void movePlayer(int dc, int dr) {
        int nc = player.col() + dc;
        int nr = player.row() + dr;
        if (grid.inBounds(nc, nr)) player.setPosition(nc, nr);
        for (int i = 0; i < drops.size(); i++) {
            DroppedItem d = drops.get(i);
            if (d.col == player.col() && d.row == player.row()) {
                inventory.add(d.item);
                drops.remove(i);
                i--;
            }
        }
 
    }

    public void paint(Graphics g, Point mouse, int ww, int wh) {
        if (!started) {
            g.setColor(Color.BLACK);
            g.drawString("Press Enter to start. WASD/Arrows to move. I for inventory.", 20, 30);
            return;
        }
        int offsetX = ww / 2 - player.col() * Cell.SIZE - Cell.SIZE / 2;
        int offsetY = wh / 2 - player.row() * Cell.SIZE - Cell.SIZE / 2;
        for (DroppedItem d : drops) d.render(g, offsetX, offsetY);


        grid.paint(g, mouse, offsetX, offsetY, viewCols, viewRows, player.col(), player.row());
        for (Actor a : actors) a.render(g, offsetX, offsetY);

        if (showInventory) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(8, 8, 200, 120);
            g.setColor(Color.WHITE);
            g.drawString("Inventory", 16, 28);
            int y = 48;
            for (var e : inventory.snapshot().entrySet()) {
                g.drawString(e.getKey() + " x" + e.getValue(), 16, y);
                y += 16;
            }
        }
    }
}
