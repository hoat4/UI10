package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.decoration.Style;

public class StyleableContainerView extends StyleableView<Container, Style> {

    public StyleableContainerView(Container model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        if (model.contentProp.get() == null)
            throw new IllegalStateException("model not initialized yet: "+model);
        return model.contentProp.get();
    }
}
