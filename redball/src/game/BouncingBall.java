package game;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class BouncingBall extends MIDlet {
    final WhiteCanvas canvas = new WhiteCanvas();

    void gameLoop() {
        while (true) {
            canvas.clear();
            canvas.bounceBall();
            canvas.moveBall();
            canvas.drawBall();
            canvas.flushGraphics();
        }
    }

    protected void startApp() throws MIDletStateChangeException {
        Display.getDisplay(this).setCurrent(canvas);
        new Thread(new Runnable() {
            public void run() {
                gameLoop();
            }
        }).start();
    }

    protected void pauseApp() { }

    protected void destroyApp(boolean b) throws MIDletStateChangeException { }

    static class WhiteCanvas extends GameCanvas {
        private Graphics g;

        WhiteCanvas() {
            super(true);
            setFullScreenMode(true);
            g = getGraphics();
        }

        void clear() {
            g.setColor(0xffffff);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        float yPos = 0, yVel = 0.1f;
        int dm = 40;
        void drawBall() {
            g.setColor(0xff0000);
            g.fillRoundRect(30, (int)yPos, dm, dm, dm, dm);
        }

        void moveBall() {
            yPos += yVel;
        }

        void bounceBall() {
            if (yPos < 0 || yPos > getHeight() - dm) {
                yVel = -yVel;
            }
        }
    }
}
