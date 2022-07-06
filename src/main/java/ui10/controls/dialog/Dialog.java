package ui10.controls.dialog;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.util.Collections;
import java.util.List;

public abstract class Dialog extends Element {

    public String title() {
        return null;
    }

    public String headingText() {
        return null;
    }

    public abstract Kind kind(); // default ikonon kívül mi másra?

    public Element icon() { // ha van heading, abba kerül, ha nincs, contenttől balra
        return null;
    }

    // lehet text helyett description, contentText is a neve
    // ez az inputok fölött vna
    public abstract String text();

    // wrong name, it may be a message or data, not only "input"
    public List<DialogInput> inputs() {
        return Collections.emptyList();
    }

    public abstract List<DialogButton> actions();

    public abstract DialogButton defaultAction();

    public Element content() { // text alá kerül. bár input dialognál meg mellé.
        return null;
    }

    public List<Element> footers() {
        return Collections.emptyList();
    }

    public abstract class DialogButton extends Element {

        public abstract DialogActionKind kind();

        public abstract void performAction();

        public abstract String text();

        public boolean enabled() {
            return true;
        }

        public String description() { // FX-ben "longText"
            return null;
        }

        public Element icon() { // FX-ben "graphic"
            return null;
        }

        public interface DialogActionKind {
        }

        // JFX-ben van ButtonType és ButtonData, utána kéne nézni azoknak, meg hogy más platformokon hogy csinálják
        public enum DialogActionStandardKind implements DialogActionKind {

            APPLY, CANCEL, CLOSE, FINISH, NEXT, NO, OK, PREVIOUS, YES
        }
    }


    public interface Kind {
    }

    public enum StandardKind implements Kind {
        INFORMATION, WARNING, ERROR, QUESTION
    }
}
