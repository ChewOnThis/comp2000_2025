import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Main().run()); }

    class Canvas extends JPanel {
        private Stage stage;
        private final Set<Integer> pressed = new HashSet<>();
        private Timer moveTimer;

        Canvas() {
            setPreferredSize(new Dimension(800, 600));
            setFocusable(true);
            requestFocusInWindow();

            int worldCols = 50,worldRows = 50;
            int seed = (int)(System.currentTimeMillis() & 0x7fffffff);

            stage = new Stage(worldCols, worldRows, seed);

            // Smooth movement when speed powerup is active
            moveTimer = new Timer(12, e -> {
                if (!stage.hasSpeedPowerup) return;
                int dc = 0, dr = 0;
                if (pressed.contains(KeyEvent.VK_A) || pressed.contains(KeyEvent.VK_LEFT)) dc -= 1;
                if (pressed.contains(KeyEvent.VK_D) || pressed.contains(KeyEvent.VK_RIGHT)) dc += 1;
                if (pressed.contains(KeyEvent.VK_W) || pressed.contains(KeyEvent.VK_UP)) dr -= 1;
                if (pressed.contains(KeyEvent.VK_S) || pressed.contains(KeyEvent.VK_DOWN)) dr += 1;
                if (dc != 0 || dr != 0) { stage.movePlayerBy(dc, dr); repaint(); }
            });
            moveTimer.start();

            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    pressed.add(e.getKeyCode());
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_ENTER -> { stage.startGame(); repaint(); }
                        case KeyEvent.VK_W, KeyEvent.VK_UP -> { if (!stage.hasSpeedPowerup) { stage.movePlayerBy(0, -1); repaint(); } }
                        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> { if (!stage.hasSpeedPowerup) { stage.movePlayerBy(0, 1); repaint(); } }
                        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> { if (!stage.hasSpeedPowerup) { stage.movePlayerBy(-1, 0); repaint(); } }
                        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> { if (!stage.hasSpeedPowerup) { stage.movePlayerBy(1, 0); repaint(); } }
                        case KeyEvent.VK_I, KeyEvent.VK_E -> { stage.toggleInventory(); repaint(); } // support E
                        case KeyEvent.VK_F -> { stage.pickupHere(); repaint(); } // pickup
                        case KeyEvent.VK_SPACE -> { stage.attack(); repaint(); } // attack
                        case KeyEvent.VK_R -> { stage.resetWorld(); repaint(); } // reset
                        case KeyEvent.VK_P -> { // optional: quick cell size adjust
                            String in = JOptionPane.showInputDialog(Main.this, "New cell size:", Integer.toString(Cell.SIZE));
                            try {
                                if (in != null) stage.changeCellSize(Math.max(6, Integer.parseInt(in.trim())));
                            } catch (Exception ignore) {}
                            repaint();
                        }
                    }
                }
                @Override public void keyReleased(KeyEvent e) { pressed.remove(e.getKeyCode()); }
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
