package ui10;

import ui10.binding.Scope;
import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TextButton;
import ui10.controls.TextField;
import ui10.decoration.Box;
import ui10.decoration.Decoration;
import ui10.decoration.RuleBasedDecoration;
import ui10.geom.Insets;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.image.LinearGradient;
import ui10.image.RGBColor;
import ui10.layout.Centered;
import ui10.layout.Padding;
import ui10.layout.StackPane;
import ui10.nodes.*;
import ui10.renderer.java2d.AWTDesktop;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.window.Desktop;
import ui10.window.Window;

import java.util.List;
import java.util.Map;

import static ui10.geom.Point.ORIGO;

public class Main3 {


    static final RGBColor fxbase = RGBColor.ofRGB(0xECECEC);
    static final     RGBColor fxbg = fxbase.derive(.264);

    static final RGBColor fxctrlinnerbg = RGBColor.WHITE;
    static final RGBColor fxmidtextcolor = RGBColor.ofRGBShort(0x333);
    static final RGBColor textColor = fxmidtextcolor; // ladderral számoljá bg-ből
    static final RGBColor fxtextboxborder = fxbg.derive(-.15);

    public static void main(String[] args) {
        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;
        eventLoop.runLater(()->{

            RuleBasedDecoration d = new RuleBasedDecoration(Map.of(
                    Button.TAG, List.of(
                            Decoration.ofReplace((container, p, s) -> {
                                FilledRectanglePane filledPane = new FilledRectanglePane();
                                filledPane.fill.bindTo(((Button<?>) container).pressed(),
                                        b -> RGBColor.ofRGBShort(b ? 0xCC2 : 0xEE3)); // TODO scope

                                return new StackPane(filledPane,
                                        new Padding(container.units.px(10), p));
                            })
                    ),
                    TextButton.LABEL_TAG, List.of(
                            Decoration.ofModify((p, s) -> ((Label) p).textStyle.set(AWTTextStyle.of(20)))
                    ),
                    TextField.TAG, List.of(
                            Decoration.ofReplace((container, p, s)->textFieldDecoration((TextField) container, p, s))
                    ),
                    TextField.TEXT_TAG, List.of(
                            Decoration.ofModify((p, scope)->((TextPane)p).textColor.set(textColor, scope))
                    )
            ));

            TextButton button = new TextButton("Gomb");
            Padding padding = new Padding(0, 0, 0, 0, button);
            button.onClick.subscribe(v->{
//            padding.top.b(padding.top.get() + button.units.px(15));
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
        });
    }

    private static Box textFieldDecoration(TextField textField, Node content, Scope scope) {
        textField.textStyle.set(AWTTextStyle.of(16));

        Box box = new Box(content);

        box.background.set(new LinearGradient(ORIGO, new Point(0, textField.units.px(5)), List.of(
                new LinearGradient.Stop(fxctrlinnerbg.derive(-.09), 0),
                new LinearGradient.Stop(fxctrlinnerbg, 1))), scope);

        box.borderStyle.bindTo(content.bounds.nullsafeMap(Rectangle::size).map(size->{
            if (size == null)
                return new Border.BorderStyle(1000, RGBColor.BLACK, 2000); // TODO

            final LinearGradient borderGradient = new LinearGradient(ORIGO, size.leftBottom(), List.of(
                    new LinearGradient.Stop(fxtextboxborder.derive(-.1), 0),
                    new LinearGradient.Stop(fxtextboxborder, 1)));
            return new Border.BorderStyle(1000, borderGradient, 2000);
        }), scope);
        box.padding.set(new Insets(textField.units.px(5)));
        return box;
    }
}
