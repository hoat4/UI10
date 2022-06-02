package ui10.control4;

import ui10.base.Control;
import ui10.base.Element;
import ui10.base.ElementModel;
import ui10.decoration.d3.Decoration;

public abstract class ControlView2<M extends ElementModel<?>, D extends Decoration>
        extends Control implements ElementModel.ElementModelListener {

    // ennek lehet hogy inkább protectednek kéne lennie, de DecoratorElementnek kell egyelőre
    public final M model;

    private D decoration;

    public ControlView2(M model) {
        this.model = model;
    }

    protected D decoration() {
        return decoration;
    }

    @Override
    protected void initLogicalParent(Element logicalParent) {
        super.initLogicalParent(logicalParent);
        decoration = uiContext().viewProvider().makeDecoration(this);
    }

    @Override
    protected Element content() {
        return decoration.wrapContent(contentImpl());
    }

    protected abstract Element contentImpl();
}
