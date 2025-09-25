// Renderable: anything that can draw itself onto a Graphics context.
import java.awt.Graphics;

public interface Renderable {
    void render(Graphics g, int offsetX, int offsetY);
}
