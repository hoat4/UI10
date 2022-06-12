package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.decoration.Style;
import ui10.decoration.views.StyleableView;

public class StyleableContainerView extends StyleableView<Container, Style> {

    public StyleableContainerView(Container model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return model.getContent();
    }
}
