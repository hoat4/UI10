package ui10.controls.dialog;

import ui10.base.Element;
import ui10.binding3.Model;

import java.util.Collections;
import java.util.List;

public interface DialogContent extends Model {

    default String title() {
        return null;
    }

    default String headingText() {
        return null;
    }

    Kind kind(); // default ikonon kívül mi másra?

    default Element icon() { // ha van heading, abba kerül, ha nincs, contenttől balra
        return null;
    }

    // lehet text helyett description, contentText is a neve
    // ez az inputok fölött vna
    String text();

    // wrong name, it may be a message or data, not only "input"
    default List<DialogInput> inputs() {
        return Collections.emptyList();
    }

    List<DialogAction> actions();

    default Element content() { // text alá kerül. bár input dialognál meg mellé.
        return null;
    }

    default List<Element> footers() {
        return Collections.emptyList();
    }


    // legyen inkább composition inheritance helyett? így most nem lehet meglévő actionöket DialogActionként használni
    interface DialogAction extends Action {

        DialogActionKind kind();

        interface DialogActionKind {
        }

        // JFX-ben van ButtonType és ButtonData, utána kéne nézni azoknak, meg hogy más platformokon hogy csinálják
        enum DialogActionStandardKind implements DialogActionKind {

            APPLY, CANCEL, CLOSE, FINISH, NEXT, NO, OK, PREVIOUS, YES
        }
    }


    interface Kind {
    }

    enum StandardKind implements Kind {
        INFORMATION, WARNING, ERROR, QUESTION
    }
}
