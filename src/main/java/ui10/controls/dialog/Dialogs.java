package ui10.controls.dialog;

import ui10.binding2.ElementEvent;
import ui10.binding3.PropertyIdentifier;
import ui10.controls.Action;
import ui10.window.Desktop;

import java.util.List;
import java.util.function.Consumer;

public class Dialogs {

    public static void showMessage(String text) {
        MessageDialogContent content = new MessageDialogContent(text);
        showDialog(content);
    }

    public static void showInput(String text) {
        MessageDialogContent content = new MessageDialogContent(text);
        showDialog(content);
    }

    private static void showDialog(MessageDialogContent content) {
        throw new UnsupportedOperationException("TODO");
        /*
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

        //e = new CSSDecorator(e, css);

        Window w = Window.of(e);
        w.focusContext.defaultAction.set(content.defaultAction());
        content.desktop = desktop;
        content.window = w;

        desktop.windows.add(w);

         */
    }

    private static class MessageDialogContent implements DialogContent {

        Desktop desktop;
//        Window window;

        private final String text;
        private DialogAction okAction = new DialogAction() {

            @Override
            public void performAction() {
  // TODO              desktop.windows.remove(window);
            }

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
        };

        public MessageDialogContent(String text) {
            this.text = text;
        }

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
            return List.of(okAction);
        }

        @Override
        public Action defaultAction() {
            return okAction;
        }

        @Override
        public void subscribe(Consumer<? super ElementEvent> consumer, PropertyIdentifier... properties) {
        }
    }

    private static class TextInputDialogContent implements DialogContent {

        Desktop desktop;
        //Window window;

        private final String text;
        private DialogAction okAction = new DialogAction() {

            @Override
            public void performAction() {
          // TODO      desktop.windows.remove(window);
            }

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
        };

        public TextInputDialogContent(String text) {
            this.text = text;
        }

        @Override
        public Kind kind() {
            return StandardKind.QUESTION;
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public List<DialogAction> actions() {
            return List.of(okAction);
        }

        @Override
        public Action defaultAction() {
            return okAction;
        }

        @Override
        public void subscribe(Consumer<? super ElementEvent> consumer, PropertyIdentifier... properties) {
        }
    }
}
