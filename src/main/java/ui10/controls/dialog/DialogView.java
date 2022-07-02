package ui10.controls.dialog;

import ui10.base.Container;
import ui10.base.Element;
import ui10.controls.TextView;

import static ui10.binding3.PropertyIdentifier.prop;
import static ui10.layout.Layouts.vertically;

// .dialog
public class DialogView extends Container {

    public final DialogContent content;

    public DialogView(DialogContent content) {
        this.content = content;
        content.subscribe(e -> {
            invalidate();
        }, prop(DialogContent::text));

     //   withClass("dialog", this);
    }

    @Override
    protected Element content() {
        return vertically(
                main(),
                buttonBar()
        );
    }

    // .dialog-main
    Element main() {
        return new TextView(content.text());
    }

    Element buttonBar() {
        /*List<Element> buttons = new ArrayList<>();
        for (DialogContent.DialogAction action : content.actions()) {
            buttons.add(new Button.OfAction(action));
        }
        return withClass("dialog-button-bar",
                halign(HorizontalAlignment.RIGHT, horizontally(buttons))
        );

         */
        return null;
    }
}
