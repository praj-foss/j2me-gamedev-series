package game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class RedBall extends MIDlet {
    protected void startApp() throws MIDletStateChangeException {
        Canvas canvas = new Canvas() {
            protected void paint(Graphics g) {
                g.setColor(0xffffff);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(0x0000ff);
                g.fillRoundRect(30, 30, 40, 40, 40, 40);
            }
        };
        Display.getDisplay(this).setCurrent(canvas);
    }

    protected void pauseApp() { }

    protected void destroyApp(boolean b) throws MIDletStateChangeException { }
}
