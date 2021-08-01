package ui10;

import ui10.decoration.Decoration;
import ui10.decoration.DecorationPane;
import ui10.decoration.RuleBasedDecoration;
import ui10.image.RGBColor;
import ui10.node.EventLoop;
import ui10.nodes2.*;
import ui10.renderer.java2d.AWTDesktop;
import ui10.renderer.java2d.AWTTextStyle;

import java.util.List;
import java.util.Map;

import static ui10.geom.Num.ZERO;
import static ui10.geom.Num.num;

public class Main2 {
    public static void main(String[] args) {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;

        RuleBasedDecoration d = new RuleBasedDecoration(Map.of(
                Button.TAG, List.of(
                        Decoration.ofReplace((container, p, s) -> {
                            FilledPane filledPane = new FilledPane();
                            filledPane.color().bindTo(((Button<?>) container).pressed(),
                                    b -> RGBColor.ofRGBShort(b ? 0xCC2 : 0xEE3)); // TODO scope

                            return new StackPane(filledPane, new Padding(num(10), p));
                        })
                ),
                TextButton.LABEL_TAG, List.of(
                        Decoration.ofModify((p, s) -> ((Label) p).textStyle().set(AWTTextStyle.of(12)))
                )
        ));

        TextButton button = new TextButton("Gomb");
        Padding padding = new Padding(ZERO, ZERO, ZERO, ZERO, button);
        button.onClick().subscribe(v->{
            padding.top().set(padding.top().get().add(num(15)));
        });
        Centered content = new Centered(padding);
        DecorationPane dp = new DecorationPane(content, d);

        Window window = new Window(dp);
        desktop.windows().add(window);
        window.shown().getAndSubscribe(b -> {
            if (!b)
                System.exit(0);
        });
    }
}
