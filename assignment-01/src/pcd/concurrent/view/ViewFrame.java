package pcd.concurrent.view;

import pcd.concurrent.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame {

    private final VisualiserPanel panel;
    private final ViewModel model;
    private final RenderSynch sync;

    public ViewFrame(ViewModel model, int w, int h) {
        this.model = model;
        this.sync = new RenderSynch();
        setTitle("Poool Game - multithreaded version");
        setSize(w, h + 25);
        setResizable(false);
        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }

            public void windowClosed(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    public void render() {
        long nf = sync.nextFrameToRender();
        panel.repaint();
        try {
            sync.waitForFrameRendered(nf);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public class VisualiserPanel extends JPanel {
        private final int ox;
        private final int oy;
        private final int delta;

        private final Font labelFont = new Font("Arial", Font.BOLD, 18);
        private final Font scoreFont = new Font("Arial", Font.BOLD, 64);

        public VisualiserPanel(int w, int h) {
            setSize(w, h + 25);
            ox = w / 2;
            oy = h / 2;
            delta = Math.min(ox, oy);
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());

            //holes
            g2.setColor(Color.BLACK);
            int holeRad = (int) (Board.HOLE_RADIUS * delta);
            //left hole
            int hlx = (int) (ox + Board.LEFT_HOLE_POS.x() * delta);
            int hly = (int) (oy - Board.LEFT_HOLE_POS.y() * delta);
            g2.fillOval(hlx - holeRad, hly - holeRad, holeRad * 2, holeRad * 2);
            //right hole
            int hrx = (int) (ox + Board.RIGHT_HOLE_POS.x() * delta);
            int hry = (int) (oy - Board.RIGHT_HOLE_POS.y() * delta);
            g2.fillOval(hrx - holeRad, hry - holeRad, holeRad * 2, holeRad * 2);

            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ox, 0, ox, oy * 2);
            g2.drawLine(0, oy, ox * 2, oy);
            g2.setColor(Color.BLACK);

            g2.setStroke(new BasicStroke(1));
            for (var b : model.getBalls()) {
                var p = b.pos();
                int x0 = (int) (ox + p.x() * delta);
                int y0 = (int) (oy - p.y() * delta);
                int radiusX = (int) (b.radius() * delta);
                int radiusY = (int) (b.radius() * delta);
                g2.drawOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
            }

            g2.setFont(labelFont);
            g2.setColor(Color.BLACK);

            // Palla Player ('H' in basso a sinistra)
            var pb = model.getPlayerBall();
            if (pb != null) {
                drawLabeledBall(g2, pb, "H");
            }

            // Palla Bot ('B' in basso a destra)
            var bb = model.getBotBall();
            if (bb != null) {
                drawLabeledBall(g2, bb, "B");
            }

            g2.setFont(scoreFont);
            g2.setColor(Color.BLUE);

            // Punteggio Player
            int sx_p = (int)(ox + (-1.0) * delta);
            int sy_p = (int)(oy - (-0.2) * delta);
            drawScore(g2, sx_p, sy_p, model.getPlayerScore());

            // Punteggio Bot
            int sx_b = (int)(ox + (1.0) * delta);
            int sy_b = (int)(oy - (-0.2) * delta);
            drawScore(g2, sx_b, sy_b, model.getBotScore());

//            g2.setStroke(new BasicStroke(1));
//            g2.drawString("Num small balls: " + model.getBalls().size(), 20, 40);
//            g2.drawString("Frame per sec: " + model.getFramePerSec(), 20, 60);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(Color.GRAY);
            g2.drawString("FPS: " + model.getFramePerSec(), 20, 20);

            sync.notifyFrameRendered();

        }

        private void drawLabeledBall(Graphics2D g2, BallViewInfo b, String label) {
            int x0 = (int)(ox + b.pos().x() * delta);
            int y0 = (int)(oy - b.pos().y() * delta);
            int r = (int)(b.radius() * delta);

            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x0 - r, y0 - r, r * 2, r * 2);

            drawCenteredString(g2, label, x0, y0);
        }

        private void drawScore(Graphics2D g2, int x, int y, int score) {
            String scoreStr = String.valueOf(score);
            drawCenteredString(g2, scoreStr, x, y);
        }

        private void drawCenteredString(Graphics2D g2, String s, int x, int y) {
            FontMetrics fm = g2.getFontMetrics();
            int width = fm.stringWidth(s);
            int height = fm.getAscent();

            int tx = x - (width / 2);
            int ty = y + (height / 2) - fm.getDescent();

            g2.drawString(s, tx, ty);
        }

    }
}
