
public interface Actor extends Renderable, Updatable {
    int col();
    int row();
    void setPosition(int c, int r);
    boolean isDead();
    void damage(int d);
}

