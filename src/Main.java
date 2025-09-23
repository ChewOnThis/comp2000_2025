import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Main().run()); }

    class Canvas extends JPanel {
        private Stage stage;
        private final Set<Integer> pressedKeys = new HashSet<>();

        Canvas() {
            setPreferredSize(new Dimension(800, 600));
            setFocusable(true);
            requestFocusInWindow();

            // Default world size and seed
            int worldCols = 60, worldRows = 45;
            int seed = (int)(System.currentTimeMillis() & 0x7fffffff);

            stage = new Stage(worldCols, worldRows, seed);

            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    pressedKeys.add(e.getKeyCode());
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_ENTER -> { stage.startGame(); repaint(); }
                        case KeyEvent.VK_W, KeyEvent.VK_UP -> { stage.movePlayer(0, -1); repaint(); }
                        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> { stage.movePlayer(0, 1); repaint(); }
                        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> { stage.movePlayer(-1, 0); repaint(); }
                        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> { stage.movePlayer(1, 0); repaint(); }
                        case KeyEvent.VK_I -> { stage.toggleInventory(); repaint(); }
                    }
                }
                @Override public void keyReleased(KeyEvent e) { pressedKeys.remove(e.getKeyCode()); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            stage.paint(g, getMousePosition(), getWidth(), getHeight());
        }
    }

    private void run() {
        setTitle("Grid Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new Canvas());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
