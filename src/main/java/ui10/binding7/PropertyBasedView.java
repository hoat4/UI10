package ui10.binding7;

import ui10.decoration.Style;
import ui10.decoration.views.StyleableView;

public abstract class PropertyBasedView<M extends PropertyBasedModel<?>, S extends Style> extends StyleableView<M, S> implements PropertyBasedModel.PropertyBasedModelListener {

    public PropertyBasedView(M model) {
        super(model);
    }

    @Override
    protected final void validate() {
        validateImpl();
        decoration().invalidated(model.dirtyProperties());
        model.dirtyProperties().clear();
    }

    protected void validateImpl() {
    }

    @Override
    public void modelInvalidated() {
        invalidate();
    }
}
