package ui10.base;

import ui10.binding9.OVal;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.util.function.Consumer;

public non-sealed abstract class RootElement extends Element {

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
    protected void applyShape(Shape shape, LayoutContext2 context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        throw new UnsupportedOperationException();
    }
}
