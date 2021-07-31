package ui10;

import ui10.font.TextStyle;
import ui10.geom.Num;
import ui10.node.EventLoop;
import ui10.nodes2.*;
import ui10.renderer.java2d.AWTDesktop;
import ui10.renderer.java2d.AWTTextStyle;

public class Main2 {
    public static void main(String[] args) {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;

        TextStyle textStyle = AWTTextStyle.of(12);

        TextPane textPane = new TextPane(textStyle, "Hello world!");

        Window window = new Window(new Centered(textPane));
        desktop.windows().add(window);
        window.shown().getAndSubscribe(b->{
            if (!b)
                System.exit(0);
        });
    }
}
