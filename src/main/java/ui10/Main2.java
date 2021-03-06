/*package ui10;

import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TextButton;
import ui10.controls.TextField;
import ui10.decoration.Decoration;
import ui10.decoration.DecorationPane;
import ui10.decoration.RuleBasedDecoration;
import ui10.image.RGBColor;
import ui10.layout.Centered;
import ui10.layout.Padding;
import ui10.layout.StackPane;
import ui10.base.EventLoop;
import ui10.nodes2.*;
import ui10.shell.renderer.java2d.AWTDesktop;
import ui10.shell.renderer.java2d.AWTTextStyle;

import java.util.List;
import java.util.Map;




public class Main2 {
    public static void main(String[] args) {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;

        RuleBasedDecoration d = new RuleBasedDecoration(Map.of(
                Button.TAG, List.of(
                        Decoration.ofReplace((container, p, s) -> {
                            FilledPane filledPane = new FilledPane();
                            filledPane.fill().bindTo(((Button<?>) container).pressed(),
                                    b -> RGBColor.ofRGBShort(b ? 0xCC2 : 0xEE3)); // TODO scope

                            return new StackPane(filledPane, new Padding(num(10), p));
                        })
                ),
                TextButton.LABEL_TAG, List.of(
                        Decoration.ofModify((p, s) -> ((Label) p).textStyle().set(AWTTextStyle.of(12)))
                ),
                TextField.TAG, List.of(
                        Decoration.ofReplace((container, p, s)->{
                            FilledPane filledPane = new FilledPane();
                            filledPane.fill().bindTo(((TextField) container).focused(),
                                    b -> RGBColor.ofRGBShort(b ? 0xFFF : 0xDDD)); // TODO scope

                            return new StackPane(filledPane, new Padding(num(10), p));
                        })
                ),
                TextField.LABEL_TAG, List.of(
                        Decoration.ofModify((p, s) -> ((Label) p).textStyle().set(AWTTextStyle.of(12)))
                )
        ));

        TextButton button = new TextButton("Gomb");
        Padding padding = new Padding(ZERO, ZERO, ZERO, ZERO, button);
        button.onClick().subscribe(v->{
            padding.top().set(padding.top().get().add(num(15)));
        });
        final TextField f = new TextField();
        f.text().set("sz??veg");
        Centered content = new Centered(f);
        DecorationPane dp = new DecorationPane(content, d);

        Window window = new Window(dp);
        desktop.windows().add(window);
        window.shown().getAndSubscribe(b -> {
            if (!b)
                System.exit(0);
        });
    }
}
*/