package game;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class ExampleApp extends MIDlet {
    Alert alert;

    protected void startApp() {
        alert = new Alert("Hello World!");
        alert.setTimeout(Alert.FOREVER);
        Display.getDisplay(this).setCurrent(alert);
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean b) {}
}
