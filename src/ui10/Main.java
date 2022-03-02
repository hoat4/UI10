
/*
package ui10;

import ui10.animation.Animation;
import ui10.controls.Button;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.image.RGBColor;
import ui10.node.*;
import ui10.nodes.*;
import ui10.shell.renderer.java2d.AWTDesktop;

import java.time.Duration;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = null;//new AWTDesktop(eventLoop).desktop;

        Window window = new Window();
        Node r = new FixedSize(new RectangleNode(RGBColor.RED), new Size(100, 100));
        AnchorPane p = new AnchorPane(
                new AnchorPane.AnchoredItem(new Point(50, 50), r),
                new AnchorPane.AnchoredItem(new Point(0, 0), new LineNode(new Point(100, 100)))
        );
        window.content().set(new Centered(new Button("Gomb")));
        desktop.windows().add(window);
        window.shown().getAndSubscribe(b->{
            if (!b)
                System.exit(0);
        });

        Thread.sleep(1000);
        AnchorPane.AnchoredItem i = new AnchorPane.AnchoredItem(
                new Point(200, 200),
                new FixedSize(new RectangleNode(RGBColor.BLUE), new Size(100, 100))
        );
        p.items().add(i);
        Thread.sleep(1000);
        Animation.playTransition(i.pos(), Animation.Interpolator.FOR_POINTS, Animation.EasingFunction.VACAK,
                eventLoop, new Point(50, 200), new Point(200, 200),
                Duration.ofMillis(1000));
        /*for (int j = 0; j < 200; j++) {
            Thread.sleep(10);
            int k = j;
            eventLoop.runLater(()-> {
                i.pos().set(new Point(50 + k, 200, 0));
            });
        }*//*

    }
}
*/