package ui10.controls.dialog;

import ui10.Main7;
import ui10.shell.awt.AWTDesktop;
import ui10.window.Desktop;
import ui10.window.Window;

import java.util.List;

public class Dialogs {

    public static void showMessage(String text) {
        MessageDialog content = new MessageDialog(text);
        showDialog(content);
    }

    public static void showInput(String text) {
        MessageDialog content = new MessageDialog(text);
        showDialog(content);
    }

    private static void showDialog(MessageDialog content) {
        content.depParent = Main7.main;
        Desktop desktop = AWTDesktop.instance();

        Window w = Window.of(content);
        //w.focusContext.defaultAction.set(content.defaultAction());
        content.desktop = desktop;
        desktop.windows.add(content);
    }

    private static class MessageDialog extends Dialog {

        Desktop desktop;
//        Window window;

        private final String text;
        private DialogButton okAction = new DialogButton() {

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
/*
            @Override
            public void subscribe(Consumer<? super ElementEvent> consumer, PropertyIdentifier... properties) {
            }*/
        };

        public MessageDialog(String text) {
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
        public List<DialogButton> actions() {
            return List.of(okAction);
        }

        @Override
        public DialogButton defaultAction() {
            return okAction;
        }
    }

    private static class TextInputDialog extends Dialog {

        Desktop desktop;
        //Window window;

        private final String text;
        private DialogButton okAction = new DialogButton() {

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
        };

        public TextInputDialog(String text) {
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
        public List<DialogButton> actions() {
            return List.of(okAction);
        }

        @Override
        public DialogButton defaultAction() {
            return okAction;
        }
    }
}
