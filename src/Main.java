import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Main().run()); }

    class Canvas extends JPanel {
        private Stage stage;

        Canvas() {
            setPreferredSize(new Dimension(800, 600));
            setFocusable(true);
            requestFocusInWindow();

            int worldCols = 50,worldRows = 50;
            int seed = (int)(System.currentTimeMillis() & 0x7fffffff);

            stage = new Stage(worldCols, worldRows, seed);

            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_ENTER -> { stage.startGame(); repaint(); }
                        case KeyEvent.VK_W, KeyEvent.VK_UP -> { stage.movePlayerBy(0, -1); repaint(); } // use alias
                        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> { stage.movePlayerBy(0, 1); repaint(); }
                        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> { stage.movePlayerBy(-1, 0); repaint(); }
                        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> { stage.movePlayerBy(1, 0); repaint(); }
                        case KeyEvent.VK_I, KeyEvent.VK_E -> { stage.toggleInventory(); repaint(); } // support E
                        case KeyEvent.VK_F -> { stage.pickupHere(); repaint(); } // new pickup
                        case KeyEvent.VK_SPACE -> { stage.attack(); repaint(); } // new attack
                        case KeyEvent.VK_R -> { stage.resetWorld(); repaint(); } // new reset
                    }
                }
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
