package ui10;

import ui10.geom.Fraction;
import ui10.geom.Insets;
import ui10.image.Colors;
import ui10.layout.GrowFactor;
import ui10.base.Element;
import ui10.controls.Button;
import ui10.controls.TextField;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSParser;
import ui10.decoration.css.CSSScanner;
import ui10.graphics.ColorFill;
import ui10.layout.Layouts;
import ui10.renderer.java2d.AWTDesktop;
import ui10.window.Window;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import static ui10.decoration.css.CSSClass.withClass;
import static ui10.layout.Layouts.*;

public class Main6 {

    public static void main(String[] args) {
        // System.out.println(Colors.WHITE.derive(-.09));

        AWTDesktop desktop = new AWTDesktop();
        TextField tf = new TextField();

        Button button = new Button();
        button.onAction.subscribe(__ -> System.out.println("Hello world!"));

        button.attributes().add(new GrowFactor(Fraction.of(0)));
        tf.attributes().add(new GrowFactor(Fraction.of(0)));

        Element content = withClass("root",
                centered(vertically(button, tf))

                //grid(3,
                //        new Button(), new Button(), new Button(),
                //        new Button(), new Button(), new Button()
                //)

                //centered(roundRectangle(10, stack(
                //         Layouts.padding(new ColorFill(Colors.RED), new Insets(25)), new ColorFill(Colors.GREEN)
                //)))
        );

        CSSParser css;
        try (Reader r = new InputStreamReader(Main6.class.getResourceAsStream("modena-imitation.css"))) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        content = new CSSDecorator(content, css);

        /*Window window = Window.of(centered(withSize(
                new Opacity(new ColorFill(Colors.RED), Fraction.of(.1, 100)),
                new Size(100, 100)
        )));*/
        Window window = Window.of(content);
        desktop.windows.add(window);
    }


    private static Element firstContent() {
        return Layouts.padding(
                Layouts.stack(
                        new ColorFill(Colors.YELLOW),
                        Layouts.padding(
                                Layouts.roundRectangle(20, new ColorFill(Colors.GREEN)),
                                new Insets(50)
                        )
                ),
                new Insets(50)
        );
    }


    private static Element roundedRects() {
        Element content = centered(
                wrapWithClass("ow", withClass("cn", empty()))
        );

        CSSParser css;
        try (Reader r = new StringReader("""
                .cn { background: #48a; margin: 20px; border-radius: 20px; min-width: 100px; min-height: 100px; }
                .ow { border: 10px solid #f00; }
                """)) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new CSSDecorator(content, css);
    }

}
