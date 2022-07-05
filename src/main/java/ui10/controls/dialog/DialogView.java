package ui10.controls.dialog;

import ui10.base.Container;
import ui10.base.Element;
import ui10.controls.TextView;
import ui10.decoration.DecorationContext;
import ui10.decoration.Style;
import ui10.decoration.views.StyleableView;

import java.util.ArrayList;
import java.util.List;

import static ui10.layout.Layouts.horizontally;
import static ui10.layout.Layouts.vertically;

// .dialog
public class DialogView extends StyleableView<Dialog, DialogView.DialogStyle> {

    public DialogView(Dialog content) {
        super(content);
    }

    @Override
    protected Element contentImpl() {
        TextView textView = new TextView(model.text());
        return vertically(
                decoration().dialogMain(textView),
                decoration().dialogButtonBar(horizontally(model.actions()))
        );
    }

    public interface DialogStyle extends Style {

        // .dialog-main
        Element dialogMain(Element element);

        // .dialog-button-bar
        Element dialogButtonBar(Element element);
    }
}
