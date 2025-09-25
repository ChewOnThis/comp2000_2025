import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Stage {
    private Grid grid;
    private final Player player;
    private final List<Actor> actors = new ArrayList<>();
    private final List<DroppedItem> drops = new ArrayList<>();
    private final int viewCols = 30, viewRows = 22;
    private boolean started = false;
    private boolean showInventory = false;
    private final Random rng = new Random(0x9e3779b9);

    public Stage(int cols, int rows, int seed) {
        this.grid = new Grid(cols, rows, seed);
        this.player = new Player(cols / 2, rows / 2);
        actors.add(player);
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
            Item item = (i % 3 == 0) ? new SpeedPowerup() : new Potion();
            drops.add(new DroppedItem(c, r, item));
        }
    }

    public void startGame() { started = true; }
    public void toggleInventory() { showInventory = !showInventory; }

    public void movePlayer(int dc, int dr) {
        int nc = player.col() + dc;
        int nr = player.row() + dr;
        if (grid.passable(nc, nr)) {
            player.setPosition(nc, nr);
        }
        for (int i = 0; i < drops.size(); i++) {
            DroppedItem d = drops.get(i);
            if (d.col == player.col() && d.row == player.row()) {
                player.inventory().add(d.item);
                drops.remove(i);
                i--;
            }
        }
    }

    // new: convenience alias used by improved input
    public void movePlayerBy(int dc, int dr) { movePlayer(dc, dr); }

    // new: pick up items within radius 1 (including diagonals and current cell)
    public void pickupHere() {
        int px = player.col(), py = player.row();
        int[][] dirs = {
            {0,0},{1,0},{-1,0},{0,1},{0,-1},
            {1,1},{1,-1},{-1,1},{-1,-1}
        };
        Iterator<DroppedItem> it = drops.iterator();
        while (it.hasNext()) {
            DroppedItem d = it.next();
            for (int[] dir : dirs) {
                if (d.col == px + dir[0] && d.row == py + dir[1]) {
                    player.inventory().add(d.item);
                    it.remove();
                    break;
                }
            }
        }
    }

    // new: attack all adjacent and same-cell enemies; drop a basic item on kill
    public void attack() {
        int px = player.col(), py = player.row();
        int[][] dirs = {
            {0,0},{1,0},{-1,0},{0,1},{0,-1},
            {1,1},{1,-1},{-1,1},{-1,-1}
        };
        // apply damage
        for (Actor a : actors) {
            if (a instanceof Enemy e) {
                for (int[] dir : dirs) {
                    int cx = px + dir[0], cy = py + dir[1];
                    if (e.col() == cx && e.row() == cy) {
                        e.damage(1);
                        break;
                    }
                }
            }
        }
        // remove dead enemies and drop loot
        Iterator<Actor> it = actors.iterator();
        while (it.hasNext()) {
            Actor a = it.next();
            if (a instanceof Enemy e && e.isDead()) {
                it.remove();
                drops.add(new DroppedItem(new Potion(), e.col(), e.row()));
            }
        }
    }

    // new: regenerate world, reposition player, respawn enemies and items
    public void resetWorld() {
        int cols = grid.columns, rows = grid.rows;
        int seed = (int)(System.currentTimeMillis() & 0x7fffffff);
        Grid newGrid = new Grid(cols, rows, seed);
        // swap in the new grid
        this.grid = newGrid;

        // reset actors and drops; keep the same player object
        this.actors.clear();
        this.drops.clear();
        this.player.setPosition(cols / 2, rows / 2);
        this.actors.add(player);

        Random local = new Random(seed ^ 0x9e3779b9);
        for (int i = 0; i < 24; i++) {
            int c = local.nextInt(cols), r = local.nextInt(rows);
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
            int c = local.nextInt(cols), r = local.nextInt(rows);
            Item item = (i % 3 == 0) ? new SpeedPowerup() : new Potion();
            drops.add(new DroppedItem(c, r, item));
        }
    }

    // removed invalid changeCellSize; Cell.SIZE is a constant in this project

    public void paint(Graphics g, Point mouse, int ww, int wh) {
        if (!started) {
            g.setColor(Color.BLACK);
            g.drawString("Press Enter to start. WASD/Arrows to move. I for inventory.", 20, 30);
            return;
        }
        int offsetX = ww / 2 - player.col() * Cell.SIZE - Cell.SIZE / 2;
        int offsetY = wh / 2 - player.row() * Cell.SIZE - Cell.SIZE / 2;

        // Draw terrain first, then items, then actors so items are not hidden by tiles
        grid.paint(g, mouse, offsetX, offsetY, viewCols, viewRows, player.col(), player.row());
        for (DroppedItem d : drops) d.render(g, offsetX, offsetY);
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
