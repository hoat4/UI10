package ui10.renderer6.java2d;

import ui10.geom.Insets;
import ui10.image.Colors;
import ui10.image.RGBColor;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.Element;
import ui10.ui6.decoration.*;
import ui10.ui6.decoration.css.CSSParser;
import ui10.ui6.decoration.css.CSSScanner;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.graphics.TextNode;
import ui10.ui6.layout.Layouts;
import ui10.ui6.controls.TextField;
import ui10.ui6.window.Window;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static ui10.ui6.decoration.css.CSSClass.withClass;
import static ui10.ui6.layout.Layouts.*;

public class Main6 {

    public static void main(String[] args) {
        System.out.println(Colors.WHITE.derive(-.09));

        AWTDesktop desktop = new AWTDesktop();
        TextField tf = new TextField();

        TextNode tn = new TextNode();
        tn.text("Hello world!");
        tn.textStyle(AWTTextStyle.of(24));
        tn.fill(new ColorFill(RGBColor.ofRGB(0x333333)));

        Element content = centered(tf);

        CSSParser css;
        try (Reader r = new StringReader("""
                .text-field {
                    padding: 5px;
                    border: 1px solid linear-gradient(to bottom, #CDCDCD, #B8B8B8);
                    background: linear-gradient(from 0px 0px to 0px 5px, #E8E8E8, #fff);
                    border-radius: 5px;
                 }
                .text { font-size: 16px; color: #323232; }
                """)) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        content = new Decorated(content, css);

        Window window = Window.of(content);
        desktop.windows.add(window);
    }


    private static Element firstContent() {
        return Layouts.padding(
                Layouts.stack(
                        new ColorFill(Colors.YELLOW),
                        Layouts.padding(
                                Layouts.shaped(
                                        Layouts.roundRectangle(20, new ColorFill(Colors.GREEN))
                                ),
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

        return new Decorated(content, css);
    }

}
