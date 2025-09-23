import java.awt.Graphics;

public interface Actor {
    int col();
    int row();
    void setPosition(int c, int r);
    boolean isDead();
    void damage(int d);
    void render(Graphics g, int offsetX, int offsetY);
}
