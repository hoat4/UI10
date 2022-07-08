package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.controls.Button;
import ui10.controls.TextView;
import ui10.controls.dialog.Dialog;

public class DialogButtonView extends Container {

    private final Dialog.DialogButton model;

    public DialogButtonView(Dialog.DialogButton model) {
        this.model = model;
    }

    @Override
    protected Element content() {
        Button button = new Button(new TextView(model.text()), model::performAction);
        if (model.kind() == Dialog.DialogButton.DialogActionStandardKind.OK)
            button.role.set(Button.Role.DEFAULT);
        return button;
    }
}
