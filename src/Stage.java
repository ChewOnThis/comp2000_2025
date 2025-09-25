import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Stage {
    Font boldFont = new Font("Papyrus", Font.BOLD, 15);

    private int WORLD_COLS;
    private int WORLD_ROWS;
    private int seed = (int)(System.currentTimeMillis() & 0x7fffffff);

    private Grid grid = new Grid(WORLD_COLS, WORLD_ROWS, seed);
    private Player player = new Player(WORLD_COLS/2, WORLD_ROWS/2);
    private List<Enemy> enemies = new ArrayList<>();
    private List<DroppedItem> drops = new ArrayList<>();
    private Inventory<Item> inventory = new Inventory<>();

    private int offsetX = 0, offsetY = 0;
    private int viewCols = 30, viewRows = 22;

    private EnemyFactory enemyFactory = new EnemyFactory(seed);

    public boolean hasSpeedPowerup = false;
    private DroppedItem speedPowerup = null;

    private boolean showInventory = false;
    private long startTime = 0;
    private long endTime = 0;

    private double moveAccumulatorCol = 0;
    private double moveAccumulatorRow = 0;

    private static class TrailPoint {
        Point pos; long timestamp;
        TrailPoint(Point pos, long timestamp) { this.pos = pos; this.timestamp = timestamp; }
    }

    private Deque<TrailPoint> playerTrail = new ArrayDeque<>();
    private int trailLength = 10;
    private long trailDurationMs = 500;

    public enum GameState { INTRO, PLAYING, END }
    private GameState state = GameState.INTRO;

    private final Map<Enemy, String> enemyBiomeMap = new HashMap<>();

    public Stage(int worldCols, int worldRows) {
        this.WORLD_COLS = worldCols;
        this.WORLD_ROWS = worldRows;
        seed = (int)(System.currentTimeMillis() & 0x7fffffff);
        grid = new Grid(WORLD_COLS, WORLD_ROWS, seed);
        player = new Player(WORLD_COLS/2, WORLD_ROWS/2);
        enemies = new ArrayList<>();
        drops = new ArrayList<>();
        inventory = new Inventory<>();
        enemyFactory = new EnemyFactory(seed);
        showInventory = false;
        startTime = 0;
        endTime = 0;
        state = GameState.INTRO;
        hasSpeedPowerup = false;
        moveAccumulatorCol = 0;
        moveAccumulatorRow = 0;
        spawnEnemiesByBiome();
        spawnSpeedPowerup();
    }

    public void resetWorld() {
        seed = (int)(System.currentTimeMillis() & 0x7fffffff);
        grid = new Grid(WORLD_COLS, WORLD_ROWS, seed);
        player = new Player(WORLD_COLS/2, WORLD_ROWS/2);
        enemies.clear();
        drops.clear();
        inventory = new Inventory<>();
        enemyFactory = new EnemyFactory(seed);
        state = GameState.PLAYING;
        startTime = System.currentTimeMillis();
        endTime = 0;
        hasSpeedPowerup = false;
        moveAccumulatorCol = 0;
        moveAccumulatorRow = 0;
        spawnEnemiesByBiome();
        spawnSpeedPowerup();
    }

    private void spawnEnemiesByBiome() {
        enemies.clear();
        enemyBiomeMap.clear();

        Map<String, Integer> area = new HashMap<>();
        Map<String, Biome> anyBiome = new HashMap<>();

        for (int c = 0; c < WORLD_COLS; c++) {
            for (int r = 0; r < WORLD_ROWS; r++) {
                Biome b = grid.cells[c][r].getBiome();
                area.merge(b.name(), 1, Integer::sum);
                anyBiome.putIfAbsent(b.name(), b);
            }
        }

        Random rng = new Random(seed ^ 0x1234abcd);
        int totalCells = WORLD_COLS * WORLD_ROWS;
        int baseEnemies = Math.max(3, totalCells / 400);

        for (var e : area.entrySet()) {
            String biomeName = e.getKey();
            int a = e.getValue();
            int count = Math.max(1, (int)(baseEnemies * (a / (double) totalCells)));
            Biome b = anyBiome.get(biomeName);

            for (int i = 0; i < count; i++) {
                for (int tries = 0; tries < 200; tries++) {
                    int c = rng.nextInt(WORLD_COLS), r = rng.nextInt(WORLD_ROWS);
                    if (grid.cells[c][r].getBiome().name().equals(biomeName) &&
                        grid.cells[c][r].passable()) {

                        String type = enemyFactory.pickWeighted(b.enemyWeights());
                        Enemy enemy = enemyFactory.create(type, c, r, biomeName);
                        enemy.hp = 1;
                        enemies.add(enemy);
                        enemyBiomeMap.put(enemy, biomeName);
                        break;
                    }
                }
            }
        }
    }

    public void startGame() {
        state = GameState.PLAYING;
        startTime = System.currentTimeMillis();
        endTime = 0;
    }

    public void movePlayerBy(int dc, int dr) {
        if (state != GameState.PLAYING) return;
        double speed = hasSpeedPowerup ? 1.6 : 1.0;
        if (hasSpeedPowerup) {
            moveAccumulatorCol += dc * speed;
            moveAccumulatorRow += dr * speed;
            int moveCol = (int)Math.round(moveAccumulatorCol);
            int moveRow = (int)Math.round(moveAccumulatorRow);
            if (moveCol != 0 || moveRow != 0) {
                int nc = player.col() + moveCol, nr = player.row() + moveRow;
                if (grid.cellAt(nc, nr).map(Cell::passable).orElse(false)) {
                    player.setPosition(nc, nr);
                    playerTrail.addLast(new TrailPoint(new Point(nc, nr), System.currentTimeMillis()));
                    if (playerTrail.size() > trailLength) playerTrail.removeFirst();
                }
                moveAccumulatorCol -= moveCol;
                moveAccumulatorRow -= moveRow;
            }
        } else {
            int nc = player.col() + dc, nr = player.row() + dr;
            if (grid.cellAt(nc, nr).map(Cell::passable).orElse(false)) {
                player.setPosition(nc, nr);
                playerTrail.addLast(new TrailPoint(new Point(nc, nr), System.currentTimeMillis()));
                if (playerTrail.size() > trailLength) playerTrail.removeFirst();
            }
        }
    }

    public void changeCellSize(int newSize) {
        Cell.SIZE = newSize;
        grid = new Grid(WORLD_COLS, WORLD_ROWS, seed);
        playerTrail.clear();
        moveAccumulatorCol = 0;
        moveAccumulatorRow = 0;
        player.setPosition(WORLD_COLS/2, WORLD_ROWS/2);
    }

    public void attack() {
        if (state != GameState.PLAYING) return;
        int[][] dirs = {
            {1,0},{-1,0},{0,1},{0,-1},
            {1,1},{-1,-1},{1,-1},{-1,1},{0,0}
        };
        int px = player.col(), py = player.row();

        for (int[] dir : dirs) {
            int cx = px + dir[0], cy = py + dir[1];
            for (Enemy e : enemies) {
                if (e.col() == cx && e.row() == cy) e.damage(1);
            }
        }

        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            if (e.isDead()) {
                it.remove();
                String biomeName = enemyBiomeMap.getOrDefault(e, "Grassland");
                Item item = getDropForEnemy(e, biomeName);
                drops.add(new DroppedItem(item, e.col(), e.row()));
                enemyBiomeMap.remove(e);
            }
        }

        if (enemies.isEmpty() && drops.isEmpty()) {
            state = GameState.END;
            endTime = System.currentTimeMillis();
        }
    }

    public void pickupHere() {
        if (state != GameState.PLAYING) return;

        int px = player.col(), py = player.row();
        int[][] dirs = {
            {0,0},{1,0},{-1,0},{0,1},{0,-1},
            {1,1},{-1,-1},{1,-1},{-1,1}
        };

        Iterator<DroppedItem> it = drops.iterator();
        while (it.hasNext()) {
            DroppedItem d = it.next();
            for (int[] dir : dirs) {
                int cx = px + dir[0], cy = py + dir[1];
                if (d.col == cx && d.row == cy) {
                    inventory.add(d.item);
                    it.remove();
                    break;
                }
            }
        }

        if (speedPowerup != null) {
            for (int[] dir : dirs) {
                int cx = px + dir[0], cy = py + dir[1];
                if (speedPowerup.col == cx && speedPowerup.row == cy) {
                    hasSpeedPowerup = true;
                    inventory.add(speedPowerup.item);
                    speedPowerup = null;
                    break;
                }
            }
        }

        if (enemies.isEmpty() && drops.isEmpty() && speedPowerup == null) {
            state = GameState.END;
            endTime = System.currentTimeMillis();
        }
    }

    public void toggleInventory() {
        if (state == GameState.PLAYING) showInventory = !showInventory;
    }

    public void paint(Graphics g, Point mouse, int windowW, int windowH) {
        if (state == GameState.INTRO) { drawIntro(g, windowW, windowH); return; }
        if (state == GameState.END) { drawEnding(g, windowW, windowH); return; }

        offsetX = (windowW / 2) - (player.col() * Cell.SIZE + Cell.SIZE/2);
        offsetY = (windowH / 2) - (player.row() * Cell.SIZE + Cell.SIZE/2);
        viewCols = Math.max(1, windowW / Cell.SIZE);
        viewRows = Math.max(1, windowH / Cell.SIZE);

        grid.paint(g, mouse, offsetX, offsetY, viewCols, viewRows, player.col(), player.row());

        for (DroppedItem d : drops) d.render(g, offsetX, offsetY);
        for (Enemy e : enemies) e.render(g, offsetX, offsetY);
        if (speedPowerup != null) speedPowerup.render(g, offsetX, offsetY);
        player.render(g, offsetX, offsetY);

        long now = System.currentTimeMillis();
        while (!playerTrail.isEmpty() && now - playerTrail.peekFirst().timestamp > trailDurationMs) {
            playerTrail.removeFirst();
        }

        int trailSize = playerTrail.size();
        List<TrailPoint> trailList = new ArrayList<>(playerTrail);
        for (int i = 0; i < trailSize; i++) {
            TrailPoint tp = trailList.get(i);
            Point p = tp.pos;
            int px = p.x * Cell.SIZE + offsetX + Cell.SIZE/2;
            int py = p.y * Cell.SIZE + offsetY + Cell.SIZE/2;
            int size = (int)(Cell.SIZE * 0.7);
            int alpha = Math.max(30, 120 - (trailSize - i - 1) * (120 / Math.max(1, trailLength)));

            double angle;
            if (i < trailSize - 1) {
                Point next = trailList.get(i + 1).pos;
                double dx = next.x - p.x;
                double dy = next.y - p.y;
                angle = Math.atan2(dy, dx);
            } else {
                double dx = player.col() - p.x;
                double dy = player.row() - p.y;
                angle = Math.atan2(dy, dx);
            }

            Polygon triangle = new Polygon();
            for (int j = 0; j < 3; j++) {
                double a = angle + Math.PI + j * 2 * Math.PI / 3;
                int tx = px + (int)(size * Math.cos(a) / 2);
                int ty = py + (int)(size * Math.sin(a) / 2);
                triangle.addPoint(tx, ty);
            }
            g.setColor(new Color(255, 0, 0, alpha));
            g.fillPolygon(triangle);
        }

        int infoW = 220, infoH = 90;
        int infoX = windowW - infoW - 16, infoY = 16;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(infoX, infoY, infoW, infoH);
        g.setColor(Color.WHITE);
        String bName = grid.cellAt(player.col(), player.row()).map(c -> c.getBiome().name()).orElse("?");
        g.drawString("Pos: ("+player.col()+","+player.row()+")", infoX + 12, infoY + 28);
        g.drawString("Biome: " + bName, infoX + 12, infoY + 48);

        long nowTime = (state == GameState.END && endTime != 0) ? endTime : System.currentTimeMillis();
        if (startTime != 0) {
            long elapsed = (nowTime - startTime) / 1000;
            g.drawString("Time: " + elapsed + "s", infoX + 12, infoY + 68);
        }

        if (showInventory) drawInventory(g, windowW, windowH);

        if (hasSpeedPowerup) {
            g.setColor(Color.YELLOW);
            g.setFont(boldFont);
            g.drawString("Speed Powerup Active!", infoX + 10, infoY + infoH + 18);
        }
    }

    private void drawIntro(Graphics g, int windowW, int windowH) {
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0, 0, windowW, windowH);
        g.setColor(Color.WHITE);
        int y = 60;
        g.setFont(g.getFont().deriveFont(28f));
        g.drawString("Welcome to Inhale of the Wilderness!", 60, y); y += 40;
        g.setFont(g.getFont().deriveFont(18f));
        g.drawString("How to Play & Features:", 60, y); y += 30;
        g.setFont(g.getFont().deriveFont(15f));
        g.drawString("Objective:", 60, y); y += 22;
        g.drawString(" - Defeat all enemies and collect all items to win.", 80, y); y += 20;
        g.drawString(" - Explore a procedurally generated world with multiple biomes.", 80, y); y += 20;
        g.drawString(" - Each biome has unique enemies and item drops.", 80, y); y += 20;
        g.drawString(" - Collect loot and powerups to increase your score.", 80, y); y += 20;
        g.drawString("Player & Movement:", 60, y); y += 22;
        g.drawString(" - Move with Arrow keys or WASD (hold for diagonal).", 80, y); y += 20;
        g.drawString(" - Movement is step-by-step until you collect the Speed Powerup.", 80, y); y += 20;
        g.drawString(" - After collecting Speed Powerup, movement becomes smooth and 1.5x faster.", 80, y); y += 20;
        g.drawString(" - A translucent red triangle trail follows your movement, fading over time.", 80, y); y += 20;
        g.drawString("Combat & Enemies:", 60, y); y += 22;
        g.drawString(" - Press SPACE to attack all adjacent and same-cell enemies.", 80, y); y += 20;
        g.drawString(" - Enemies are 1 hit to defeat and drop biome-appropriate loot.", 80, y); y += 20;
        g.drawString(" - Enemy types and drops vary by biome (e.g., Scorpion in Desert, Wolf in Forest).", 80, y); y += 20;
        g.drawString("Items & Inventory:", 60, y); y += 22;
        g.drawString(" - Press F to pick up items in adjacent cells (radius 1, including diagonals).", 80, y); y += 20;
        g.drawString(" - Press E to open/close your inventory.", 80, y); y += 20;
        g.drawString(" - Items are stackable or unique, and each has a score value.", 80, y); y += 20;
        g.drawString(" - Speed Powerup is a special item that increases movement speed.", 80, y); y += 20;
        g.drawString("World & Map:", 60, y); y += 22;
        g.drawString(" - The world is a grid of cells, each with a biome and terrain type.", 80, y); y += 20;
        g.drawString(" - Biomes include Grassland, Desert, Water, and Forest.", 80, y); y += 20;
        g.drawString(" - Press M to change map size and cell pixel size (prompted in menu).", 80, y); y += 20;
        g.drawString(" - Press R (hold for 1 second) to reset the world.", 80, y); y += 20;
        g.drawString("Visuals & UI:", 60, y); y += 22;
        g.drawString(" - Each cell has a colored border matching its biome.", 80, y); y += 20;
        g.drawString(" - Enemies and items display nameplates above them.", 80, y); y += 20;
        g.drawString(" - Info box shows position, biome, elapsed time, and powerup status.", 80, y); y += 20;
        g.drawString(" - The clock and UI update continuously.", 80, y); y += 20;
        g.drawString("Advanced Features:", 60, y); y += 22;
        g.drawString(" - Inventory uses generics and supports stacking.", 80, y); y += 20;
        g.drawString(" - Cell, Actor, Item, and Biome use interfaces and inheritance.", 80, y); y += 20;
        g.drawString(" - The grid and biomes are generated using procedural noise.", 80, y); y += 20;
        g.drawString(" - The code demonstrates Java proficiency, inheritance, interfaces, and generics.", 80, y); y += 20;
        g.setFont(g.getFont().deriveFont(18f));
        g.drawString("Press ENTER to start your adventure!", 60, y+30);
    }

    private void drawEnding(Graphics g, int windowW, int windowH) {
        g.setColor(new Color(0,0,0,220));
        g.fillRect(0, 0, windowW, windowH);
        g.setColor(Color.WHITE);
        int y = 60;
        g.setFont(g.getFont().deriveFont(24f));
        g.drawString("Congratulations! All enemies and items collected!", 60, y); y += 40;
        g.setFont(g.getFont().deriveFont(16f));
        g.drawString("Your loot:", 60, y); y += 30;
        int totalScore = 0;
        for (String name : inventory.names().stream().sorted().collect(Collectors.toList())) {
            int count = inventory.count(name);
            int value = getItemValue(name);
            g.drawString("- " + name + " x" + count + " (worth $" + value + " each)", 80, y);
            totalScore += count * value;
            y += 22;
        }
        g.drawString("Total Score: $" + totalScore, 60, y+20);
        if (startTime != 0 && endTime != 0) {
            long elapsed = (endTime - startTime) / 1000;
            g.drawString("Your Time: " + elapsed + "s", 60, y+50);
        }
        g.drawString("Hold R to play again.", 60, y+80);
    }

    private void drawInventory(Graphics g, int windowW, int windowH) {
        g.setColor(new Color(0,0,0,160));
        g.fillRect(0, 0, windowW, windowH);
        g.setColor(Color.WHITE);
        g.drawString("Inventory (E to close):", 20, 40);
        int y = 70;
        for (String name : inventory.names().stream().sorted().collect(Collectors.toList())) {
            int value = getItemValue(name);
            g.drawString("- " + name + " x" + inventory.count(name) + " ($" + value + ")", 30, y);
            y += 18;
        }
    }

    private int getItemValue(String name) {
        switch (name) {
            case "Potion": return 10;
            case "Sword": return 25;
            case "Sand Blade": return 30;
            case "Cactus Juice": return 15;
            case "Scorpion Stinger": return 40;
            case "Water Pearl": return 20;
            case "Fish Scale": return 12;
            case "Piranha Tooth": return 35;
            case "Leaf Cloak": return 18;
            case "Wolf Fang": return 22;
            case "Ancient Bark": return 28;
            case "Speed Powerup": return 50;
            default: return 5;
        }
    }

    private Item getDropForEnemy(Enemy e, String biomeName) {
        Random rng = new Random(System.nanoTime() + e.col() * 31 + e.row() * 17);
        if ("Desert".equals(biomeName)) {
            if ("Scorpion".equals(e.type())) {
                Item[] pool = {
                    new Weapon() { public String getName(){return "Sand Blade";} },
                    new Weapon() { public String getName(){return "Cactus Juice";} },
                    new Weapon() { public String getName(){return "Scorpion Stinger";} }
                };
                return pool[rng.nextInt(pool.length)];
            } else if ("Slime".equals(e.type())) {
                Item[] pool = { new Potion(), new Weapon() { public String getName(){return "Sand Blade";} } };
                return pool[rng.nextInt(pool.length)];
            }
        } else if ("Water".equals(biomeName)) {
            if ("Piranha".equals(e.type())) {
                Item[] pool = {
                    new Weapon() { public String getName(){return "Water Pearl";} },
                    new Weapon() { public String getName(){return "Fish Scale";} },
                    new Weapon() { public String getName(){return "Piranha Tooth";} }
                };
                return pool[rng.nextInt(pool.length)];
            }
        } else if ("Forest".equals(biomeName)) {
            if ("Wolf".equals(e.type())) {
                Item[] pool = {
                    new Weapon() { public String getName(){return "Wolf Fang";} },
                    new Weapon() { public String getName(){return "Ancient Bark";} }
                };
                return pool[rng.nextInt(pool.length)];
            } else if ("Slime".equals(e.type())) {
                Item[] pool = { new Potion(), new Weapon() { public String getName(){return "Leaf Cloak";} } };
                return pool[rng.nextInt(pool.length)];
            }
        } else if ("Grassland".equals(biomeName)) {
            if ("Slime".equals(e.type())) {
                Item[] pool = { new Potion(), new Weapon() { public String getName(){return "Leaf Cloak";} } };
                return pool[rng.nextInt(pool.length)];
            } else if ("Wolf".equals(e.type())) {
                Item[] pool = { new Weapon() { public String getName(){return "Wolf Fang";} }, new Potion() };
                return pool[rng.nextInt(pool.length)];
            }
        }
        Item[] pool = { new Weapon(), new Potion() };
        return pool[rng.nextInt(pool.length)];
    }

    private void spawnSpeedPowerup() {
        Random rng = new Random(seed ^ 0xabcdef12);
        int c = rng.nextInt(WORLD_COLS), r = rng.nextInt(WORLD_ROWS);
        speedPowerup = new DroppedItem(new SpeedPowerup(), c, r);
    }
}
