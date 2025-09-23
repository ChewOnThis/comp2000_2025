import java.awt.Graphics;

public interface Actor extends Renderable, Updatable {
    int col();
    int row();
    void setPosition(int c, int r);
    boolean isDead();
    void damage(int d);
    @Override void render(Graphics g, int offsetX, int offsetY);
}
