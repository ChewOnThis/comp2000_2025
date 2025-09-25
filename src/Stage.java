import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Stage {
    private Grid grid; // was final
    private final Player player;
    private final List<Actor> actors = new ArrayList<>();
    private final List<DroppedItem> drops = new ArrayList<>();
    private final int viewCols = 30, viewRows = 22;
    private boolean started = false;
    private boolean showInventory = false;
    private final Random rng = new Random(0x9e3779b9);
    private final EnemyFactory enemyFactory; // new

    // Powerup + win/timing state
    public boolean hasSpeedPowerup = false; // exposed for Main timer usage
    private DroppedItem speedPowerup = null;
    private boolean ended = false;
    private long startTime = 0L, endTime = 0L;

    public Stage(int cols, int rows, int seed) {
        this.grid = new Grid(cols, rows, seed);
        this.player = new Player(cols / 2, rows / 2);
        this.enemyFactory = new EnemyFactory(seed); // new
        actors.add(player);
        for (int i = 0; i < 24; i++) {
            int c = rng.nextInt(cols), r = rng.nextInt(rows);
            if (!grid.passable(c, r)) { i--; continue; } // ensure spawn on passable
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

        // spawn a single distinct speed powerup instance
        spawnSpeedPowerup(cols, rows);
    }

    private void spawnSpeedPowerup(int cols, int rows) {
        for (int tries = 0; tries < 200; tries++) {
            int c = rng.nextInt(cols), r = rng.nextInt(rows);
            if (grid.passable(c, r)) {
                speedPowerup = new DroppedItem(new SpeedPowerup(), c, r);
                break;
            }
        }
    }

    public void startGame() { 
        started = true; 
        ended = false;
        hasSpeedPowerup = false;
        startTime = System.currentTimeMillis();
        endTime = 0L;
    }
    public void toggleInventory() { showInventory = !showInventory; }

    private boolean isWinCondition() {
        boolean anyEnemy = false;
        for (Actor a : actors) {
            if (a instanceof Enemy) { anyEnemy = true; break; }
        }
        return !anyEnemy && drops.isEmpty() && speedPowerup == null;
    }

    public void movePlayer(int dc, int dr) {
        if (!started || ended) return;
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
        // pick up speed powerup by walking onto it
        if (speedPowerup != null && speedPowerup.col == player.col() && speedPowerup.row == player.row()) {
            hasSpeedPowerup = true;
            player.inventory().add(speedPowerup.item);
            speedPowerup = null;
        }
        if (isWinCondition()) {
            ended = true;
            endTime = System.currentTimeMillis();
        }
    }

    // new: convenience alias used by improved input
    public void movePlayerBy(int dc, int dr) { movePlayer(dc, dr); }

    // new: pick up items within radius 1 (including diagonals and current cell)
    public void pickupHere() {
        if (!started || ended) return;
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
        // pickup the dedicated speed powerup if adjacent
        if (speedPowerup != null) {
            for (int[] dir : dirs) {
                if (speedPowerup.col == px + dir[0] && speedPowerup.row == py + dir[1]) {
                    hasSpeedPowerup = true;
                    player.inventory().add(speedPowerup.item);
                    speedPowerup = null;
                    break;
                }
            }
        }
        if (isWinCondition()) {
            ended = true;
            endTime = System.currentTimeMillis();
        }
    }

    // new: attack all adjacent and same-cell enemies; drop a basic item on kill
    public void attack() {
        if (!started || ended) return;
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
        if (isWinCondition()) {
            ended = true;
            endTime = System.currentTimeMillis();
        }
    }

    // regenerate world, reset state, respawn enemies, items, and powerup
    public void resetWorld() {
        int cols = grid.columns, rows = grid.rows;
        int seed = (int)(System.currentTimeMillis() & 0x7fffffff);
        this.grid = new Grid(cols, rows, seed);
        this.actors.clear();
        this.drops.clear();
        this.player.setPosition(cols / 2, rows / 2);
        this.actors.add(player);
        this.hasSpeedPowerup = false;
        this.ended = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0L;

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
        // respawn distinct powerup
        spawnSpeedPowerup(cols, rows);
    }

    // new: allow runtime cell size change
    public void changeCellSize(int newSize) {
        Cell.SIZE = Math.max(6, newSize);
    }

    public void paint(Graphics g, Point mouse, int ww, int wh) {
        if (!started) {
            g.setColor(Color.BLACK);
            g.drawString("Press Enter to start. WASD/Arrows to move. E to inventory, F to pick up, SPACE to attack, R to reset.", 20, 30);
            return;
        }
        if (ended) {
            g.setColor(new Color(0,0,0,180));
            g.fillRect(0, 0, ww, wh);
            g.setColor(Color.WHITE);
            g.drawString("All enemies defeated and items collected!", 20, 40);
            if (startTime != 0L && endTime != 0L) {
                long elapsed = (endTime - startTime)/1000;
                g.drawString("Time: " + elapsed + "s", 20, 60);
            }
            g.drawString("Press R to play again.", 20, 80);
            return;
        }

        int offsetX = ww / 2 - player.col() * Cell.SIZE - Cell.SIZE / 2;
        int offsetY = wh / 2 - player.row() * Cell.SIZE - Cell.SIZE / 2;

        // Draw terrain first, then items, then actors so items are not hidden by tiles
        grid.paint(g, mouse, offsetX, offsetY, viewCols, viewRows, player.col(), player.row());
        for (DroppedItem d : drops) d.render(g, offsetX, offsetY);
        if (speedPowerup != null) speedPowerup.render(g, offsetX, offsetY);
        for (Actor a : actors) a.render(g, offsetX, offsetY);

        // Inventory overlay
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

        // Info panel
        int infoW = 200, infoH = 70, infoX = ww - infoW - 12, infoY = 12;
        g.setColor(new Color(0,0,0,140));
        g.fillRect(infoX, infoY, infoW, infoH);
        g.setColor(Color.WHITE);
        g.drawString("Pos: (" + player.col() + "," + player.row() + ")", infoX + 10, infoY + 22);
        String bname = grid.cellAt(player.col(), player.row()).getBiome().name();
        g.drawString("Biome: " + bname, infoX + 10, infoY + 38);
        if (startTime != 0L) {
            long now = System.currentTimeMillis();
            long elapsed = ((endTime != 0L) ? endTime : now) - startTime;
            g.drawString("Time: " + (elapsed/1000) + "s", infoX + 10, infoY + 54);
        }
        if (hasSpeedPowerup) {
            g.setColor(Color.YELLOW);
            g.drawString("Speed!", infoX + 140, infoY + 22);
        }
    }
}
