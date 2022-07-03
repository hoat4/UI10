package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.binding7.InvalidationListener;
import ui10.decoration.Style;

public class StyleableContainerView extends StyleableView<Container, Style> implements InvalidationListener {

    public StyleableContainerView(Container model) {
        super(model);
    }

    @Override
    protected void validateImpl() {
        if (model.dirtyProperties().contains(ContainerProperties.CONTENT))
            contentValid = false;
    }

    @Override
    protected Element contentImpl() {
        return model.getContent();
    }
}
