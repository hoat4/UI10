
/*
package ui10;

import ui10.base.Container;
import ui10.base.Element;
import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TabbedPane;
import ui10.controls.TextField;
import ui10.controls.dialog.Dialogs;
import ui10.decoration.DecorationProvider;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSParser;
import ui10.decoration.css.CSSScanner;
import ui10.decoration.views.DecorableControlViewProvider;
import ui10.decoration.views.DecorableLabelView;
import ui10.geom.Insets;
import ui10.graphics.ColorFill;
import ui10.image.Colors;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.shell.awt.AWTDesktop;
import ui10.window.Desktop;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutionException;

import static ui10.layout.Layouts.centered;
import static ui10.layout.Layouts.vertically;

public class Main6 {

    private static TabbedPane tabbedPane;
    public static int counter;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Desktop.THREAD_LOCAL.set(new AWTDesktop());

        if (false) {
            Dialogs.showMessage("Hello world!");
            return;
        }

        // System.out.println(Colors.WHITE.derive(-.09));

        AWTDesktop desktop = new AWTDesktop();
        Window window = makeSampleWindow(desktop);
        if (window == null)
            return;

        desktop.windows.add(window);

        Thread.sleep(1500);

        /*
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

         */

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
/*
    }

    private static Window makeSampleWindow(AWTDesktop desktop) {
        CSSParser css;
        try (Reader r = new InputStreamReader(Main6.class.getResourceAsStream("modena-imitation.css"))) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        TextField tf = new TextField();
        tf.text("szövegmező");

        Button newButton = new Button();
        newButton.text("Gomb");
        newButton.action(() -> System.out.println("Hello world!"));
/*
        //button.attributes().add(new GrowFactor(Fraction.of(0)));
        //tf.attributes().add(new GrowFactor(Fraction.of(0)));

        List<String> list = List.of("Hello", "world!");

        Table<String> table = new Table<>(List.of(
                new Table.Column<>("Col1", s -> s),
                new Table.Column<>("Col2", s -> String.valueOf(s.length()))
        ), list);

        Element tableView = padding(table, new Insets(50));
        Tabs.title(tableView, "Táblázat");

        Label lm = new Label();
        lm.text("label");
        lm.setView(new DecorableLabelView(lm));

        LinearLayout vbox = vertically(
                lm,
                newButton,
                tf
        );
        vbox.gap = 10; // mértékegység?
        Element buttonTab = centered(vbox);
        //Tabs.title(buttonTab, "Egyik tab");

        buttonTab = Container.of(buttonTab);

        CSSDecorator css1 = new CSSDecorator(css);
        desktop.viewProviderChain.viewProviders.add(new DecorableControlViewProvider(css1));
        desktop.decorationProvider = new DecorationProvider(css1);
        //desktop.viewProvider.initRoot(buttonTab);
        Element content = buttonTab;
        return Window.of(content);

        /*
        content = withClass("root",

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


        //Container contentWrapper = Container.of(content);
        //content = new CSSDecorator(contentWrapper, css);

        /*Window window = Window.of(centered(withSize(
                new Opacity(new ColorFill(Colors.RED), Fraction.of(.1, 100)),
                new Size(100, 100)
        )));
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
/*

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

 */
