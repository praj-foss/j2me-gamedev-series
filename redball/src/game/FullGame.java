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
    volatile boolean isAlive = true;

    final WhiteCanvas canvas = new WhiteCanvas();
    final Board board = new Board(
            0x00af00, 10, canvas.getHeight() - 20, canvas.getWidth()/5, 16);
    final Ball ball = new Ball(0xff0000,  15, canvas.getHeight()-50, canvas.getWidth()/8);
    final BrickWall wall = new BrickWall(
            0, canvas.statHeight, canvas.getWidth(), canvas.getHeight()/3);

    int secondsLeft;
    final Timer countdown = new Timer();
    final TimerTask countdownTask = new TimerTask() {
        public void run() {
            if (secondsLeft > 0 && isAlive) {
                secondsLeft--;
            } else {
                countdown.cancel();
                ball.xVel = 0;
                ball.yVel = 0;
                isAlive = false;
            }
        }
    };

    final Runnable gameLoop = new Runnable() {
        public void run() {
            final int timePerFrame = 1000 / FRAMES_PER_S;
            long timePast = System.currentTimeMillis();
            while (isRunning) {
                checkInput();
                checkBounds();
                checkBoardCollision();
                checkWallCollision();
                moveBall();
                if (wall.isDestroyed()) {
                    endScreen("YOU WON", 0x00af00);
                    break;
                } else if (!isAlive) {
                    endScreen("YOU DIED", 0xaf0000);
                    break;
                }

                canvas.clear();
                canvas.drawBall(ball);
                canvas.drawBoard(board);
                canvas.drawBrickWall(wall);
                canvas.drawStats(secondsLeft);
                canvas.flushGraphics();

                timePast += timePerFrame;
                sleep(timePast - System.currentTimeMillis());
            }
        };

        void checkInput() {
            int ks = canvas.getKeyStates();
            if ((ks & WhiteCanvas.LEFT_PRESSED) != 0) {
                board.x -= board.speed * S_PER_FRAME;
            }
            if ((ks & WhiteCanvas.RIGHT_PRESSED) != 0) {
                board.x += board.speed * S_PER_FRAME;
            }
            board.x = Math.min(Math.max(board.x, 0), canvas.getWidth() - board.width);
        }

        void moveBall() {
            ball.x += ball.xVel * S_PER_FRAME;
            ball.y += ball.yVel * S_PER_FRAME;
        }

        void checkBounds() {
            if (ball.x < 0 ) {
                ball.xVel = -ball.xVel;
                ball.x = 1;
            } else if (ball.x > canvas.getWidth() - ball.dm) {
                ball.xVel = -ball.xVel;
                ball.x = canvas.getWidth() - ball.dm - 1;
            }
            if (ball.y < canvas.statHeight) {
                ball.yVel = -ball.yVel;
                ball.y = canvas.statHeight + 1;
            }
            if (ball.y > canvas.getHeight()) {
                isAlive = false;
            }
        }

        void checkBoardCollision() {
            if (ball.y + ball.dm < board.y) {
                return;
            }
            float x = Math.max(board.x, Math.min(ball.centerX(), board.x + board.width));
            float y = Math.max(board.y, Math.min(ball.centerY(), board.y + board.height));

            float dx = ball.centerX() - x;
            float dy = ball.centerY() - y;

            if (dx*dx + dy*dy < ball.dm*ball.dm/4f) {
                if (dx > 0) ball.xVel = Math.abs(ball.xVel);
                else if (dx < 0) ball.xVel = -Math.abs(ball.xVel);
                if (dy > 0) ball.yVel = Math.abs(ball.yVel);
                else if (dy < 0) ball.yVel = -Math.abs(ball.yVel);
            }
        }

        void checkWallCollision() {
            if (ball.y > wall.yStart + wall.h) {
                return; // ball is outside the wall
            }

            for (int i = 0; i < wall.cols; i++) {
                for (int j = 0; j < wall.rows; j++) {
                    if (wall.brickGone[i][j]) continue;

                    int brickX = wall.xStart + i*wall.brickW;
                    int brickY = wall.yStart + j*wall.brickH;

                    // clamp logic
                    float x = Math.max(brickX, Math.min(ball.centerX(), brickX + wall.brickW));
                    float y = Math.max(brickY, Math.min(ball.centerY(), brickY + wall.brickH));

                    float dx = ball.centerX() - x;
                    float dy = ball.centerY() - y;

                    if (dx*dx + dy*dy < ball.dm*ball.dm/4f) {
                        wall.removeBrick(i, j);

                        if (dx > 0) ball.xVel = Math.abs(ball.xVel);
                        else if (dx < 0) ball.xVel = -Math.abs(ball.xVel);

                        if (dy > 0) ball.yVel = Math.abs(ball.yVel);
                        else if (dy < 0) ball.yVel = -Math.abs(ball.yVel);
                    }
                }
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

        void endScreen(String text, int color) {
            canvas.showDialog(text, color);
            canvas.flushGraphics();
            sleep(5000);
            isRunning = false;
        }
    };

    protected void startApp() {
        Display.getDisplay(this).setCurrent(canvas);
        isRunning = true;
        secondsLeft = 90;
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

        void showDialog(String text, int color) {
            int midW = getWidth()/2;
            int midH = getHeight()/2;
            int textH = g.getFont().getHeight();
            int pad = getWidth()/6;

            g.setColor(0);
            g.fillRect(0, midH - pad, getWidth(), textH + pad);
            g.setColor(color);
            g.drawString(text, midW, midH, Graphics.HCENTER | Graphics.BASELINE);
        }

        void drawBall(Ball b) {
            g.setColor(b.color);
            g.fillRoundRect((int)b.x, (int)b.y, b.dm, b.dm, b.dm, b.dm);
        }

        void drawBoard(Board board) {
            g.setColor(board.color);
            g.fillRect((int)board.x, (int)board.y, board.width, board.height);
        }

        void drawBrickWall(BrickWall wall) {
            for (int i = 0; i < wall.cols; i++) {
                for (int j = 0; j < wall.rows; j++) {
                    if (wall.isEmpty(i, j)) continue;
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
        final int color, dm;    // dm = diameter
        float x = 50, y = 150, xVel = 40, yVel = -80;

        Ball(int color, float x, float y, int diameter) {
            this.color = color;
            this.dm = diameter;
            this.x = x;
            this.y = y;
        }

        float centerX() { return x + dm/2f; }
        float centerY() { return y + dm/2f; }
    }

    static class BrickWall {
        final int rows = 3, cols = 5;
        final int h, w, xStart, yStart, brickW, brickH;
        private int bricksLeft = rows * cols;
        private final boolean[][] brickGone = new boolean[cols][rows];

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

        void removeBrick(int row, int col) {
            brickGone[row][col] = true;
            bricksLeft--;
        }

        boolean isEmpty(int row, int col) {
            return brickGone[row][col];
        }

        boolean isDestroyed() {
            return bricksLeft == 0;
        }
    }

    static class Board {
        final int color, width, height;
        float x, y, speed;

        Board(int color, float x, float y, int width, int height) {
            this.color = color;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            speed = width * 1.5f;
        }
    }
}
