package ui10;

import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TextButton;
import ui10.controls.TextField;
import ui10.decoration.Box;
import ui10.decoration.Decoration;
import ui10.decoration.RuleBasedDecoration;
import ui10.geom.Size;
import ui10.image.RGBColor;
import ui10.layout.Centered;
import ui10.layout.FixedSize;
import ui10.layout.Padding;
import ui10.layout.StackPane;
import ui10.nodes.Border;
import ui10.window.Desktop;
import ui10.nodes.FilledPane;
import ui10.nodes.EventLoop;
import ui10.renderer.java2d.AWTDesktop;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.window.Window;

import java.util.List;
import java.util.Map;

import static ui10.geom.Num.ZERO;
import static ui10.geom.Num.num;

public class Main3 {
    public static void main(String[] args) {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;

        RuleBasedDecoration d = new RuleBasedDecoration(Map.of(
                Button.TAG, List.of(
                        Decoration.ofReplace((container, p, s) -> {
                            FilledPane filledPane = new FilledPane();
                            filledPane.color.bindTo(((Button<?>) container).pressed(),
                                    b -> RGBColor.ofRGBShort(b ? 0xCC2 : 0xEE3)); // TODO scope

                            return new StackPane(filledPane, new Padding(num(10), p));
                        })
                ),
                TextButton.LABEL_TAG, List.of(
                        Decoration.ofModify((p, s) -> ((Label) p).textStyle.set(AWTTextStyle.of(12)))
                ),
                TextField.TAG, List.of(
                        Decoration.ofReplace((container, p, s)->{
                            ((TextField)container).textStyle.set(AWTTextStyle.of(12));

                            Box box = new Box(p);
                            box.background.bindTo(((TextField) container).focused.
                                    map(focused -> RGBColor.ofRGBShort(0xFFF)));
                            box.borderStyle.set(new Border.BorderStyle(num(1), RGBColor.BLACK));
                            box.padding.set(num(5));
                            return box;
                        })
                )
        ));

        TextButton button = new TextButton("Gomb");
        Padding padding = new Padding(ZERO, ZERO, ZERO, ZERO, button);
        button.onClick.subscribe(v->{
            padding.top.set(padding.top.get().add(num(15)));
        });
        final TextField f = new TextField();
        f.text.set("szöveg");
        Centered content = new Centered(f);
        content.decorations().add(d);

        //Window window = new Window(new Centered(new FixedSize(new FilledPane(RGBColor.RED), new Size(num(100), num(100)))));
        //Window window = new Window(new FilledPane(RGBColor.RED));
        Window window = new Window(content);
        desktop.windows.add(window);
        window.shown.getAndSubscribe(b -> {
            if (!b)
                System.exit(0);
        });
    }
}
