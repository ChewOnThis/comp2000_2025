import java.awt.Color;
import java.awt.Graphics;

// The Player class represents the player character in the game.
// Implements the Actor interface for rendering and updating.
public class Player implements Actor {
    // The player's current column and row in the grid.
    private int col, row;

    // The player's health points.
    private int hp = 10;

    // The radius of the player's visual representation (circle).
    private final int radius = (int)(Cell.SIZE * 0.8);

    // The color used to draw the player.
    private final Color color = Color.RED;

    // The player's inventory
    private final Inventory<Item> inventory = new Inventory<>();

    // Constructor: initializes the player's position.
    public Player(int col, int row) {
        this.col = col;
        this.row = row;
    }

    // Returns the player's column.
    @Override
    public int col() { return col; }

    // Returns the player's row.
    @Override
    public int row() { return row; }

    // Sets the player's position in the grid.
    @Override
    public void setPosition(int c, int r) { this.col = c; this.row = r; }

    // Returns true if the player is dead (hp <= 0).
    @Override
    public boolean isDead() { return hp <= 0; }

    // Applies damage to the player.
    @Override
    public void damage(int d) { hp -= d; }

    // Renders the player as a filled circle with a black outline.
    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        // Calculate the pixel position of the player.
        int cx = col * Cell.SIZE + Cell.SIZE/2 + offsetX;
        int cy = row * Cell.SIZE + Cell.SIZE/2 + offsetY;
        g.setColor(color);
        g.fillOval(cx - radius/2, cy - radius/2, radius, radius);
        g.setColor(Color.BLACK);
        g.drawOval(cx - radius/2, cy - radius/2, radius, radius);
    }

    // Update method (not used, but required by Actor interface).
    @Override
    public void update(double dt) {}

    // Accessor for the player's inventory
    public Inventory<Item> inventory() { return inventory; }
}
