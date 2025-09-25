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
    private final EnemyFactory enemyFactory;

     
    public boolean hasSpeedPowerup = false; 
    private DroppedItem speedPowerup = null;
    private boolean ended = false;
    private long startTime = 0L, endTime = 0L;

    public Stage(int cols, int rows, int seed) {
        this.grid = new Grid(cols, rows, seed);
        this.player = new Player(cols / 2, rows / 2);
        this.enemyFactory = new EnemyFactory(seed); 
        actors.add(player);
        for (int i = 0; i < 24; i++) {
            int c = rng.nextInt(cols), r = rng.nextInt(rows);
            if (!grid.passable(c, r)) { i--; continue; } 
            Biome b = grid.cellAt(c, r).getBiome();
            String type = enemyFactory.pickWeighted(b.enemyWeights());
            Enemy e = enemyFactory.create(type, c, r, b.name());
            actors.add(e);
        }

        for (int i = 0; i < 8; i++) {
            int c = rng.nextInt(cols), r = rng.nextInt(rows);
            Item item = (i % 3 == 0) ? new SpeedPowerup() : new Potion();
            drops.add(new DroppedItem(item, c, r));
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


    public void movePlayerBy(int dc, int dr) { movePlayer(dc, dr); }

 
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

 
    public void attack() {
        int px = player.col(), py = player.row();
        int[][] dirs = {
            {0,0},{1,0},{-1,0},{0,1},{0,-1},
            {1,1},{1,-1},{-1,1},{-1,-1}
        };
 
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

        Iterator<Actor> it = actors.iterator();
        while (it.hasNext()) {
            Actor a = it.next();
            if (a instanceof Enemy e && e.isDead()) {
                it.remove();
                drops.add(new DroppedItem(new Potion(), e.col(), e.row()));
            }
        }
    }

   
    public void resetWorld() {
        int cols = grid.columns, rows = grid.rows;
        int seed = (int)(System.currentTimeMillis() & 0x7fffffff);
        this.grid = new Grid(cols, rows, seed);
        this.actors.clear();
        this.drops.clear();
        this.player.setPosition(cols / 2, rows / 2);
        this.actors.add(player);

        Random local = new Random(seed ^ 0x9e3779b9);
        EnemyFactory localFactory = new EnemyFactory(seed);
        for (int i = 0; i < 24; i++) {
            int c = local.nextInt(cols), r = local.nextInt(rows);
            if (!grid.passable(c, r)) { i--; continue; }
            Biome b = grid.cellAt(c, r).getBiome();
            String type = localFactory.pickWeighted(b.enemyWeights());
            Enemy e = localFactory.create(type, c, r, b.name());
            actors.add(e);
        }
        for (int i = 0; i < 8; i++) {
            int c = local.nextInt(cols), r = local.nextInt(rows);
            Item item = (i % 3 == 0) ? new SpeedPowerup() : new Potion();
            drops.add(new DroppedItem(item, c, r));
        }
    }


    public void changeCellSize(int newSize) {
        Cell.SIZE = Math.max(6, newSize);
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
       