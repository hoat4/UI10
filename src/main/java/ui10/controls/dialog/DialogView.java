package ui10.controls.dialog;

import ui10.base.Container;
import ui10.base.Element;
import ui10.base.FocusBoundary;
import ui10.controls.TextView;
import ui10.decoration.DecorationContext;
import ui10.decoration.Style;
import ui10.decoration.StyleableContainer;
import ui10.decoration.views.StyleableView;
import ui10.layout.Layouts;
import ui10.layout.LinearLayoutBuilder;

import java.util.ArrayList;
import java.util.List;

import static ui10.layout.Layouts.*;

public class DialogView extends StyleableView<Dialog, DialogView.DialogStyle> {

    public DialogView(Dialog content) {
        super(content);
    }

    @Override
    protected Element contentImpl() {
        return vertically(
                new DialogHeader(),
                new DialogMain(),
                halign(HorizontalAlignment.RIGHT, decoration().dialogButtonBar(horizontally(model.actions())))
        );
    }

    @Override
    protected Element decorate(Element content) {
        return new FocusBoundary(super.decorate(content));
    }

    public class DialogHeader extends StyleableContainer<Style> {

        @Override
        protected Element contentImpl() {
            String headingText = model.headingText() == null ? "Message" : model.headingText();
            return LinearLayoutBuilder.horizontal()
                    .add(0, valign(VerticalAlignment.CENTER, new TextView(headingText)))
                    .add(1, Layouts.empty())
                    .add(0, DialogView.this.decoration().dialogIcon(model.kind()))
                    .build();
        }
    }

    public class DialogMain extends StyleableContainer<Style> {

        @Override
        protected Element contentImpl() {
            return new TextView(model.text());
        }
    }

    public interface DialogStyle extends Style {

        // .dialog-button-bar b
        Element dialogButtonBar(Element element);

        Element dialogIcon(Dialog.Kind dialogKind);
    }
}
