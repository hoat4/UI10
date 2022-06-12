package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.di.Component;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public non-sealed abstract class RootElement extends EnduringElement {

    @Override
    public abstract <T> void collect(Class<T> type, Consumer<T> consumer);

    @Override
    public void initParent(Element parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Shape getShapeOrFail() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RenderableElement renderableElement() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        throw new UnsupportedOperationException();
    }
}
