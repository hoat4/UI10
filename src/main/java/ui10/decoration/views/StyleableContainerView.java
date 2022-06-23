package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.decoration.Style;

public class StyleableContainerView extends StyleableView<Container, Style> implements Container.ContainerListener {

    public StyleableContainerView(Container model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return model.getContent();
    }

    @Override
    public void contentChanged() {
        invalidate();
    }
}
