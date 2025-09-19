import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {
    public static void main(String[] args) throws Exception {
      Main window = new Main();
      window.run();
    }

    class Canvas extends JPanel {
      Stage stage = new Stage();
      public Canvas() {
        setPreferredSize(new Dimension(1024, 720));
      }

      @Override
      public void paint(Graphics g) {
        stage.paint(g, getMousePosition());
      }
    }

    private Main() {
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      Canvas canvas = new Canvas();
      this.setContentPane(canvas);
      this.pack();
      this.setVisible(true);

      addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> canvas.stage.moveCam(-10, 0);
                case KeyEvent.VK_RIGHT -> canvas.stage.moveCam(10, 0);
                case KeyEvent.VK_UP -> canvas.stage.moveCam(0, -10);
                case KeyEvent.VK_DOWN -> canvas.stage.moveCam(0, 10);
            }
            repaint();
        }
      });
      setFocusable(true);
    }



    public void run() {
      while(true) {
        repaint();
      }
    }
}
