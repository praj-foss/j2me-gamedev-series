package game;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;
import java.util.Timer;
import java.util.TimerTask;

public class FullGame extends MIDlet {
    final int FRAMES_PER_S = 18;
    final float S_PER_FRAME = 1f / FRAMES_PER_S;

    volatile boolean isRunning = false;

    final WhiteCanvas canvas = new WhiteCanvas();
    final Ball ball = new Ball(0xff0000, canvas.getWidth()/8);

    final BrickWall wall = new BrickWall(
            0, canvas.statHeight, canvas.getWidth(), canvas.getHeight()/3);

    int secondsLeft;
    final Timer countdown = new Timer();
    final TimerTask countdownTask = new TimerTask() {
        public void run() {
            if (secondsLeft > 0) {
                secondsLeft--;
            } else {
                countdown.cancel();
                ball.xVel = 0;
                ball.yVel = 0;
            }
        }
    };

    final Runnable gameLoop = new Runnable() {
        public void run() {
            final int timePerFrame = 1000 / FRAMES_PER_S;
            long timePast = System.currentTimeMillis();
            while (isRunning) {
                checkCollision();
                checkBounds();
                moveBall();

                canvas.clear();
                canvas.drawBall(ball);
                canvas.drawBrickWall(wall);
                canvas.drawStats(secondsLeft);
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
                ball.xVel = -ball.xVel;
            }
            if (ball.y < canvas.statHeight || ball.y > canvas.getHeight() - ball.dm) {
                ball.yVel = -ball.yVel;
            }
        }

        void checkCollision() {
            if (ball.y < wall.yStart + wall.h) {
                ball.yVel = -ball.yVel;
            }
            // todo: check brick collisions
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
        secondsLeft = 20;
        countdown.schedule(countdownTask, 1, 1000);
        new Thread(gameLoop).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean b) {
        countdown.cancel();
        isRunning = false;
    }

    static class WhiteCanvas extends GameCanvas {
        private final Graphics g;
        final int statPadding;
        final int statHeight;
        private final StringBuffer sb;

        WhiteCanvas() {
            super(true);
            setFullScreenMode(true);
            g = getGraphics();
            statPadding = 4;
            statHeight = g.getFont().getHeight() + 2*statPadding;
            sb = new StringBuffer();
        }

        void drawBall(Ball b) {
            g.setColor(b.color);
            g.fillRoundRect((int)b.x, (int)b.y, b.dm, b.dm, b.dm, b.dm);
        }

        void drawBrickWall(BrickWall wall) {
            for (int i = 0; i < wall.cols; i++) {
                for (int j = 0; j < wall.rows; j++) {
                    g.setColor(wall.brickColor(i, j));
                    g.fillRect(
                            wall.xStart + i*wall.brickW,
                            wall.yStart + j*wall.brickH,
                            wall.brickW,
                            wall.brickH
                    );
                }
            }
        }

        void drawStats(int seconds) {
            g.setColor(0);
            g.fillRect(0, 0, getWidth(), statHeight);

            String t = "TIME:";
            g.setColor(0x00ff00);
            g.drawString(t, statPadding, statPadding, Graphics.TOP | Graphics.LEFT);

            int x = g.getFont().stringWidth(t) + 3*statPadding;
            sb.delete(0, sb.length());
            sb.append(seconds);
            g.drawString(sb.toString(), x, statPadding, Graphics.TOP | Graphics.LEFT);
        }

        void clear() {
            g.setColor(0xffffff);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    static class Ball {
        final int color, dm;    // diameter
        float x = 10, y = 150, xVel = 40, yVel = -60;

        Ball(int color, int diameter) {
            this.color = color;
            this.dm = diameter;
        }
    }

    static class BrickWall {
        final int rows = 3, cols = 5;
        final int h, w, xStart, yStart, brickW, brickH;

        BrickWall(int x, int y, int width, int height) {
            h = height; w = width;
            brickW = w / cols;
            brickH = h / rows;
            xStart = x + (w % cols)/2;
            yStart = y + (h % rows)/2;
        }

        int brickColor(int row, int col) {
            return (row + col) % 2 == 0 ? 0x0000a4 : 0x000044;
        }
    }
}
