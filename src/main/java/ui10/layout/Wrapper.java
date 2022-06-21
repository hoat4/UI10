package ui10.layout;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.geom.Size;
import ui10.geom.shape.Shape;

public class Wrapper extends SingleNodeLayout{

    public Wrapper(Element content) {
        super(content);
    }

    @Override
    protected Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(content, constraints);
    }

    @Override
    protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
        return containerShape;
    }
}
