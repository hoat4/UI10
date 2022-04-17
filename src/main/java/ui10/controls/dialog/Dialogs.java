package ui10.controls.dialog;

import ui10.Main6;
import ui10.base.Container;
import ui10.base.Element;
import ui10.binding2.ElementEvent;
import ui10.binding3.PropertyIdentifier;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSParser;
import ui10.decoration.css.CSSScanner;
import ui10.shell.awt.AWTWindowImpl;
import ui10.window.Desktop;
import ui10.window.Window;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

public class Dialogs {

    public static void showMessage(String text) {
        DialogContent content = new DialogContent() {
            @Override
            public Kind kind() {
                return StandardKind.INFORMATION;
            }

            @Override
            public String text() {
                return text;
            }

            @Override
            public List<DialogAction> actions() {
                return List.of(new DialogAction() {

                    @Override
                    public String text() {
                        return "OK"; // I18N?
                    }

                    @Override
                    public DialogActionKind kind() {
                        return DialogActionStandardKind.OK;
                    }

                    @Override
                    public void subscribe(Consumer<? super ElementEvent> consumer, PropertyIdentifier... properties) {
                    }
                });
            }

            @Override
            public void subscribe(Consumer<? super ElementEvent> consumer, PropertyIdentifier... properties) {
            }
        };

        Desktop desktop = Desktop.THREAD_LOCAL.get();
        Element e = new DialogView(content);

        CSSParser css;
        try (Reader r = new InputStreamReader(Main6.class.getResourceAsStream("modena-imitation.css"))) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        e = new CSSDecorator(e, css);

        Window w = Window.of(e);
        desktop.windows.add(w);
    }
}
