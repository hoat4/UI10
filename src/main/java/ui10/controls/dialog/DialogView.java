package ui10.controls.dialog;

import ui10.base.Container;
import ui10.base.Element;
import ui10.control4.controls.ButtonModel;
import ui10.controls.Label;

import java.util.ArrayList;
import java.util.List;

import static ui10.binding3.PropertyIdentifier.prop;
import static ui10.decoration.css.CSSClass.withClass;
import static ui10.layout.Layouts.*;

public class DialogView extends Container {

    public final DialogContent content;

    public DialogView(DialogContent content) {
        this.content = content;
        content.subscribe(e -> {
            invalidate();
        }, prop(DialogContent::text));

        withClass("dialog", this);
    }

    @Override
    protected Element content() {
        return vertically(
                main(),
                buttonBar()
        );
    }

    Element main() {
        return wrapWithClass("dialog-main", new Label(content.text()));
    }

    Element buttonBar() {
        List<Element> buttons = new ArrayList<>();
        for (DialogContent.DialogAction action : content.actions()) {
            buttons.add(new ButtonModel.OfAction(action));
        }
        return withClass("dialog-button-bar",
                halign(HorizontalAlignment.RIGHT, horizontally(buttons))
        );
    }
}
