package ui10.base;

import ui10.binding.ListChange;
import ui10.binding7.InvalidationMark;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public abstract class LayoutElement extends ElementModel {

    {
        invalidate(LayoutElementProperty.LAYOUT, LayoutElementProperty.CHILDREN);
    }

    protected abstract void enumerateChildren(Consumer<Element> element);

    protected abstract Size preferredSize(BoxConstraints constraints, LayoutContext1 context1);

    protected abstract void performLayout(Shape shape, LayoutContext2 context1);

    public enum LayoutElementProperty implements InvalidationMark {

        CHILDREN, LAYOUT
    }
}
