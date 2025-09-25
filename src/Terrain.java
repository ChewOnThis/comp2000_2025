// Terrain: low-level tile material with a display color and passability flag.
import java.awt.Color;

public enum Terrain {
    GRASS(new Color(92, 181, 82), true),
    SAND(new Color(232, 216, 162), true),
    WATER(new Color(80, 146, 218), true),
    FOREST(new Color(34, 139, 34), true);

    public final Color color;
    public final boolean passable;

    Terrain(Color color, boolean passable) {
        this.color = color;
        this.passable = passable;
    }

    // Convenience accessor used by some draw code.
    public Color getColor() {
        return color;
    }
    
}
