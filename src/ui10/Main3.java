package ui10;

import ui10.binding.Scope;
import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TextButton;
import ui10.controls.TextField;
import ui10.decoration.Box;
import ui10.decoration.Decoration;
import ui10.decoration.RuleBasedDecoration;
import ui10.decoration.Tag;
import ui10.geom.Insets;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.image.Colors;
import ui10.image.LinearGradient;
import ui10.image.RGBColor;
import ui10.layout.Centered;
import ui10.layout.Grid;
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
    static final RGBColor fxbg = fxbase.derive(.264);

    static final RGBColor fxctrlinnerbg = Colors.WHITE;
    static final RGBColor fxmidtextcolor = RGBColor.ofRGBShort(0x333);
    static final RGBColor textColor = fxmidtextcolor; // ladderral számoljá bg-ből
    static final RGBColor fxtextboxborder = fxbg.derive(-.15);
    static final RGBColor buttonfxcolor = fxbase;
    static final RGBColor buttonfxouterborder = buttonfxcolor.derive(-.23);
    static final RGBColor buttonfxouterborder_pressed = buttonfxcolor.derive(-.06).derive(-.23);

    private static final Tag GRID_TAG = new Tag("main-grid");

    public static void main(String[] args) {
        System.out.println("fxtextboxborder: "+fxtextboxborder);
        System.out.println("fxtextboxborder: "+fxtextboxborder.derive(-.1));

        EventLoop eventLoop = new EventLoop();
        Desktop desktop = new AWTDesktop(eventLoop).desktop;
        eventLoop.runLater(() -> {

            RuleBasedDecoration d = new RuleBasedDecoration(Map.of(
                    Button.TAG, List.of(
                            Decoration.ofReplace((container, p, s) -> buttonDecoration((Button<?>) container, p, s))
                    ),
                    TextButton.LABEL_TAG, List.of(
                            Decoration.ofModify((p, s) -> {
                                ((Label) p).textStyle.set(AWTTextStyle.of(16));
                                ((Label) p).textColor.set(RGBColor.ofRGB(0x323232));
                            })
                    ),
                    TextField.TAG, List.of(
                            Decoration.ofReplace((container, p, s) -> textFieldDecoration((TextField) container, p, s))
                    ),
                    TextField.TEXT_TAG, List.of(
                            Decoration.ofModify((p, scope) -> ((TextPane) p).textColor.set(textColor, scope))
                    ),
                    GRID_TAG, List.of(
                            Decoration.ofModify((p, scope) -> ((Grid) p).gap.set(10000))
                    )
            ));

            TextButton button = new TextButton("Gomb");
            Padding padding = new Padding(0, 0, 0, 0, button);
            button.onClick.subscribe(v -> {
//            padding.top.b(padding.top.get() + button.units.px(15));
            });
            final TextField f = new TextField();
            f.text.set("szöveg");
            Centered content = new Centered(new Grid.GridBuilder(GRID_TAG).add(button).add(new Centered(f)).build());
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

    private static Node buttonDecoration(Button<?> button, Node content, Scope scope) {
        FilledRectanglePane bg1 = new FilledRectanglePane(RGBColor.ofIntRGBA(0xFFFFFFBA));
        bg1.radius.set(3000);

        FilledRectanglePane bg2 = new FilledRectanglePane(button.pressed().
                map(b -> b ? buttonfxouterborder_pressed : buttonfxouterborder));
        bg2.radius.set(3000);

        FilledRectanglePane bg3 = new FilledRectanglePane();
        bg3.radius.set(2000);

        FilledRectanglePane bg4 = new FilledRectanglePane();
        bg4.radius.set(1000);

        bg3.fill.bindTo(button.pressed().flatMap(b -> bg3.bounds.nullsafeMap(r -> {
            int h = r.size().height();
            return new LinearGradient(Point.ORIGO, new Point(0, h - 1), List.of(
                    new LinearGradient.Stop(RGBColor.ofRGB(0xFDFDFD).derive(b?-.06:0), 0),
                    new LinearGradient.Stop(RGBColor.ofRGB(0xE1E1E1).derive(b?-.06:0), 1)
            ));
        })));

        bg4.fill.bindTo(button.pressed().flatMap(b -> bg4.bounds.nullsafeMap(r -> {
            int h = r.size().height();
            return new LinearGradient(Point.ORIGO, new Point(0, h - 1), List.of(
                    new LinearGradient.Stop(RGBColor.ofRGB(b ? 0xE2E2E2 : 0xEFEFEF), 0),
                    new LinearGradient.Stop(RGBColor.ofRGB(b ? 0xCDCDCD : 0xD9D9D9), 1)
            ));
        })));

        StackPane sp = new StackPane(
                bg1,
                new Padding(0, 0, 1000, 0, bg2),
                new Padding(1000, 1000, 2000, 1000, bg3),
                new Padding(2000, 2000, 3000, 2000, bg4),
                new Padding(4000, 8000, content)
        );
        return new Padding(500, 0, 500, 500, sp); // TODO
    }

    private static Box textFieldDecoration(TextField textField, Node content, Scope scope) {
        textField.textStyle.set(AWTTextStyle.of(16));

        Box box = new Box(content);

        box.background.set(new LinearGradient(ORIGO, new Point(0, textField.units.px(5)), List.of(
                new LinearGradient.Stop(fxctrlinnerbg.derive(-.09), 0),
                new LinearGradient.Stop(fxctrlinnerbg, 1))), scope);

        box.borderStyle.bindTo(content.bounds.nullsafeMap(Rectangle::size).map(size -> {
            if (size == null)
                return new Border.BorderStyle(1000, Colors.BLACK, 2000); // TODO

            final LinearGradient borderGradient = new LinearGradient(ORIGO, size.leftBottom(), List.of(
                    new LinearGradient.Stop(fxtextboxborder.derive(-.1), 0),
                    new LinearGradient.Stop(fxtextboxborder, 1)));
            return new Border.BorderStyle(1000, borderGradient, 2000);
        }), scope);
        box.padding.set(new Insets(textField.units.px(5)));
        return box;
    }
}
