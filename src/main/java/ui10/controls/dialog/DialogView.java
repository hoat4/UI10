package ui10.controls.dialog;

import ui10.base.Element;
import ui10.base.FocusBoundary;
import ui10.controls.TextView;
import ui10.decoration.StyleableContainer;
import ui10.decoration.views.ElementName;
import ui10.decoration.views.StyleableView;
import ui10.graphics.ImageData;
import ui10.graphics.ImageView;
import ui10.layout.Layouts;
import ui10.layout.LinearLayoutBuilder;

import static ui10.layout.Layouts.*;

@ElementName("Dialog")
public class DialogView extends StyleableView<Dialog> {

    public DialogView(Dialog content) {
        super(content);
    }

    @Override
    protected Element contentImpl() {
        return new FocusBoundary(vertically(
                new DialogHeader(),
                new DialogMain(),
                halign(HorizontalAlignment.RIGHT, new DialogButtonBar())
        ));
    }

    public class DialogHeader extends StyleableContainer {

        @Override
        protected Element contentImpl() {
            String headingText = model.headingText() == null ? "Message" : model.headingText();
            return LinearLayoutBuilder.horizontal()
                    .add(0, valign(VerticalAlignment.CENTER, new TextView(headingText)))
                    .add(1, Layouts.empty())
                    .add(0, dialogIcon(model.kind()))
                    .build();
        }

        private Element dialogIcon(Dialog.Kind kind) {
            return new ImageView(ImageData.of(css.resource("dialog-information.png")));
        }
    }

    public class DialogMain extends StyleableContainer {

        @Override
        protected Element contentImpl() {
            return new TextView(model.text());
        }
    }

    // .dialog-button-bar b
    public class DialogButtonBar extends StyleableContainer {

        @Override
        protected Element contentImpl() {
            return horizontally(model.actions());
        }
    }
}
