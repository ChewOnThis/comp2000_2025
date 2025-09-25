import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().run());
    }

    class Canvas extends JPanel {
        Stage stage;
        private final Set<Integer> pressedKeys = new HashSet<>();
        private long rPressedTime = 0;
        private boolean rResetTriggered = false;
        private Timer movementTimer;

        public Canvas(int worldCols, int worldRows) {
            setPreferredSize(new Dimension(1000, 1000));
            setFocusable(true);

            stage = new Stage(worldCols, worldRows);

            new Timer(50, e -> {
                if (pressedKeys.contains(KeyEvent.VK_R) && rPressedTime != 0 && !rResetTriggered) {
                    long held = System.currentTimeMillis() - rPressedTime;
                    if (held >= 1000) {
                        stage.resetWorld();
                        repaint();
                        rResetTriggered = true;
                    }
                }
            }).start();

            movementTimer = new Timer(10, e -> {
                if (stage == null) return;
                if (!stage.hasSpeedPowerup) return;
                int dc = 0, dr = 0;
                if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A)) dc -= 1;
                if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) dc += 1;
                if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W)) dr -= 1;
                if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) dr += 1;
                if (dc != 0 || dr != 0) {
                    stage.movePlayerBy(dc, dr);
                    repaint();
                }
            });
            movementTimer.start();

            new Timer(1000 / 30, e -> repaint()).start();

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    pressedKeys.add(e.getKeyCode());
                    if (stage == null) return;

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        stage.startGame();
                        repaint();
                        return;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_M) {
                        int cols = 50, rows = 50, cellSize = Cell.SIZE;
                        try {
                            String inputCols = JOptionPane.showInputDialog(Main.this, "Enter number of columns for the map size (default 50):", "50");
                            String inputRows = JOptionPane.showInputDialog(Main.this, "Enter number of rows for the map size (default 50):", "50");
                            String inputSize = JOptionPane.showInputDialog(Main.this, "Enter cell size in pixels (default " + Cell.SIZE + "):", Integer.toString(Cell.SIZE));
                            cols = Math.max(5, Integer.parseInt(inputCols.trim()));
                            rows = Math.max(5, Integer.parseInt(inputRows.trim()));
                            cellSize = Math.max(2, Integer.parseInt(inputSize.trim()));
                        } catch (Exception ex) {
                            // defaults
                        }
                        Cell.SIZE = cellSize;
                        Canvas newCanvas = new Canvas(cols, rows);
                        Main.this.setContentPane(newCanvas);
                        Main.this.pack();
                        newCanvas.requestFocusInWindow();
                        newCanvas.stage.startGame();
                        return;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_P) {
                        try {
                            String inputSize = JOptionPane.showInputDialog(Main.this, "Enter new cell size (default 35):", Integer.toString(Cell.SIZE));
                            int newSize = Math.max(2, Integer.parseInt(inputSize.trim()));
                            stage.changeCellSize(newSize);
                            repaint();
                        } catch (Exception ex) {}
                        return;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_E) {
                        stage.toggleInventory();
                        repaint();
                        return;
                    }

                    if (!stage.hasSpeedPowerup) {
                        int dc = 0, dr = 0;
                        if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A)) dc -= 1;
                        if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) dc += 1;
                        if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W)) dr -= 1;
                        if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) dr += 1;
                        if (dc != 0 || dr != 0) {
                            stage.movePlayerBy(dc, dr);
                            repaint();
                        }
                    }

                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_F: stage.pickupHere(); break;
                        case KeyEvent.VK_SPACE: stage.attack(); break;
                        case KeyEvent.VK_R:
                            if (rPressedTime == 0) {
                                rPressedTime = System.currentTimeMillis();
                                rResetTriggered = false;
                            }
                            break;
                    }
                    repaint();
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    pressedKeys.remove(e.getKeyCode());
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        rPressedTime = 0;
                        rResetTriggered = false;
                    }
                }
            });

            SwingUtilities.invokeLater(this::requestFocusInWindow);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            stage.paint(g, getMousePosition(), getWidth(), getHeight());
        }
    }

    private Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Inhale of the Wilderness");
        Canvas canvas = new Canvas(50, 50);
        setContentPane(canvas);
        pack();
        setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void run() {
        Canvas canvas = (Canvas) getContentPane();
        new Timer(1000 / 30, e -> canvas.repaint()).start();
    }
}
