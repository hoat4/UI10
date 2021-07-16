package ui10;

import ui10.animation.Animation;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.node.*;
import ui10.nodes.AnchorPane;
import ui10.nodes.Desktop;
import ui10.nodes.LineNode;
import ui10.nodes.RectangleNode;
import ui10.renderer.java2d.AWTDesktop;

import java.time.Duration;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;

        Window window = new Window();
        RectangleNode r = new RectangleNode(new Size(100, 100, 0));
        AnchorPane p = new AnchorPane(
                new AnchorPane.AnchoredItem(r, new Point(50, 50, 0)),
                new AnchorPane.AnchoredItem(new LineNode(new Point(100, 100, 0)), new Point(0, 0, 0))
        );
        window.content().set(p);
        desktop.windows().add(window);
        window.shown().getAndSubscribe(b->{
            if (!b)
                System.exit(0);
        });

        Thread.sleep(1000);
        AnchorPane.AnchoredItem i = new AnchorPane.AnchoredItem(new RectangleNode(
                new Size(100, 100, 0)), new Point(200, 200, 0));
        p.items().add(i);
        Thread.sleep(1000);
        Animation.playTransition(i.pos(), Animation.Interpolator.FOR_POINTS, Animation.EasingFunction.VACAK,
                eventLoop, new Point(50, 200, 0), new Point(200, 200, 0),
                Duration.ofMillis(1000));
        /*for (int j = 0; j < 200; j++) {
            Thread.sleep(10);
            int k = j;
            eventLoop.runLater(()-> {
                i.pos().set(new Point(50 + k, 200, 0));
            });
        }*/

    }
}
