package game;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

public class FullGame extends MIDlet {
    final int FRAMES_PER_S = 18;
    final float S_PER_FRAME = 1f / FRAMES_PER_S;

    volatile boolean isRunning = false;

    final WhiteCanvas canvas = new WhiteCanvas();
    final Ball ball = new Ball();

    final Runnable gameLoop = new Runnable() {
        public void run() {
            final int timePerFrame = 1000 / FRAMES_PER_S;
            long timePast = System.currentTimeMillis();
            while (isRunning) {
                // todo: add bricks, collisions, HUD
                checkBounds();
                moveBall();
                canvas.clear();
                canvas.drawBall(ball);
                canvas.flushGraphics();

                timePast += timePerFrame;
                sleep(timePast - System.currentTimeMillis());
            }
        };

        void moveBall() {
            ball.x += ball.xVel * S_PER_FRAME;
            ball.y += ball.yVel * S_PER_FRAME;
        }

        void checkBounds() {
            if (ball.x < 0 || ball.x > canvas.getWidth() - ball.dm) {
                ball.xVel *= -1;
            }
            if (ball.y < 0 || ball.y > canvas.getHeight() - ball.dm) {
                ball.yVel *= -1;
            }
        }

        void sleep(long duration) {
            if (duration <= 0) return;
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    };

    protected void startApp() {
        Display.getDisplay(this).setCurrent(canvas);
        isRunning = true;
        new Thread(gameLoop).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean b) {
        isRunning = false;
    }

    static class WhiteCanvas extends GameCanvas {
        private final Graphics g;
        WhiteCanvas() {
            super(true);
            setFullScreenMode(true);
            g = getGraphics();
        }

        void drawBall(Ball b) {
            g.setColor(b.color);
            g.fillRoundRect((int)b.x, (int)b.y, b.dm, b.dm, b.dm, b.dm);
        }

        void clear() {
            g.setColor(0xffffff);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    static class Ball {
        final int color = 0xff0000;
        final int dm = 30;    // diameter

        float x = 0;
        float y = 0;
        float xVel = 50;
        float yVel = 50;
    }
}
