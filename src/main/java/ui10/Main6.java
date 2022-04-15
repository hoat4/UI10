package ui10;

import ui10.base.Element;
import ui10.base.Pane;
import ui10.controls.*;
import ui10.decoration.Fill;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSParser;
import ui10.decoration.css.CSSScanner;
import ui10.geom.Insets;
import ui10.graphics.ColorFill;
import ui10.graphics.TextNode;
import ui10.image.Colors;
import ui10.layout.Grid;
import ui10.layout.Layouts;
import ui10.shell.awt.AWTDesktop;
import ui10.shell.awt.AWTWindowImpl;
import ui10.shell.awt.UIContextImpl;
import ui10.window.Window;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static ui10.decoration.css.CSSClass.withClass;
import static ui10.layout.Layouts.*;

public class Main6 {

    private static TabbedPane tabbedPane;
    public static int counter;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // System.out.println(Colors.WHITE.derive(-.09));

        AWTDesktop desktop = new AWTDesktop();
        Window window = makeSampleWindow();
        if (window == null)
            return;

        desktop.windows.add(window);

        Thread.sleep(1500);

        while (1 == "ab".length()) {
            UIContextImpl uiContext = ((AWTWindowImpl) window.rendererData).renderer.uiContext;
            CompletableFuture<Void> cf = new CompletableFuture<>();
            uiContext.eventLoop().runLater(() -> {
                long t = System.nanoTime();
                window.revalidate();
                long t2 = System.nanoTime();
                //System.out.println((t2 - t) / 1000);
                cf.complete(null);
            });
            cf.get();
        }

            /*
        while (true) {
            desktop.windows.add(window);
            if (true)
                return;
            Thread.sleep(2000);
            desktop.windows.remove(window);
            Thread.sleep(1000);
        }

             */

        // contentWrapper.setProperty(TextNode.TEXT_FILL_PROPERTY, new Fill.ColorFill(Colors.RED));
        // TODO ennek újra kéne dekorálnia a textnode-okat, amelyikhez eljut az új érték
    }

    private static Window makeSampleWindow() {
        TextField tf = new TextField();
        tf.text.set("szövegmező");

        Button button = new Button("Gomb");
        button.onAction().subscribe(__ -> System.out.println("Hello world!"));

        //button.attributes().add(new GrowFactor(Fraction.of(0)));
        //tf.attributes().add(new GrowFactor(Fraction.of(0)));

        List<String> list = List.of("Hello", "world!");

        Element tableView = padding(new Table<>(List.of(
                new Table.TableColumn<>("Col1", s -> s),
                new Table.TableColumn<>("Col2", s -> String.valueOf(s.length()))
        ), list), new Insets(50));
        Tabs.title(tableView, "Táblázat");

        Element vbox = vertically(
                new Label("label"),
                button,
                tf
        );
        vbox.setProperty(Grid.GAP_PROPERTY, 10);
        Element buttonTab = centered(vbox);
        Tabs.title(buttonTab, "Egyik tab");

        Element content = withClass("root",

                //tableView
                tabbedPane = new TabbedPane(List.of(
                        buttonTab,
                        tableView
                ))

                //firstContent()
                //centered(new FontTest())
                //centered(button)
                //centered(vertically(button, tf))

                //new Label("asdf")

                //new FlowLayout(List.of(new Label("asdfasdfasdfasdfasdfasdfasdfasdfasdf "),
                //                      new Label("fdsafdsafdsafdsafdsafdsafdsa"), new Label(" asdf")))

                //centered(withClass("main", grid(2,
                //        new Label("\u0628\u0623\u062a"), tf,
                //        new Button("\uD83D\uDC4C"), button
                //)))

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
            return null;
        }

        Pane contentWrapper = Pane.of(content);
        content = new CSSDecorator(contentWrapper, css);

        /*Window window = Window.of(centered(withSize(
                new Opacity(new ColorFill(Colors.RED), Fraction.of(.1, 100)),
                new Size(100, 100)
        )));*/
        return Window.of(content);
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
