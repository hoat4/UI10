package ui10.base;

import ui10.di.Component;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public sealed abstract class Element implements Component
        permits ElementModel, RenderableElement, RootElement {

    public final List<ElementExtra> extras = new ArrayList<>();
    // content of this field should be deleted if child is removed from container, but this is not implemented
    public Element parent;

    public Element parent() {
        return parent;
    }

    public <T> T lookup(Class<T> clazz) {
        List<T> list = lookupMultiple(clazz);
        if (list.isEmpty())
            throw new RuntimeException("unknown context type: " + clazz);
        if (list.size() > 1)
            throw new RuntimeException("multiple values found for context type " + clazz.getName() + ": " + list);
        return list.get(0);
    }

    public <T> List<T> lookupMultiple(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        collect(clazz, o->{
            Objects.requireNonNull(o);
            list.add(o);
        });
        return list;
    }

    @Override
    public <T> void collect(Class<T> type, Consumer<T> consumer) {
        if (parent != null)
            parent.collect(type, consumer);
    }

    public FocusContext focusContext() {
        return lookup(FocusContext.class);
    }

    public RenderableElement parentRenderable() {
        Element e = parent;
        while (!(e instanceof RenderableElement r)) {
            if (e == null)
                return null;
            e = e.parent;
        }
        return r;
    }

    public Point origin() {
        return getShapeOrFail().bounds().topLeft();
    }

    public abstract Shape getShapeOrFail();

    public abstract RenderableElement renderableElement();

    protected abstract void applyShape(Shape shape, LayoutContext2 context);

    /**
     * This can be used by decorators to walk the elementClass tree. When encountering a Pane, the decorator should
     * set the Pane.decorator field because Panes usually recreate its children every time, so decorating them only once
     * is useless.
     */
    protected abstract void enumerateStaticChildren(Consumer<Element> consumer); // this does not honor replacement

    public abstract void initParent(Element parent);

    public <E extends ElementExtra> E extra(Class<E> elementExtraClass) {
        for (ElementExtra e : extras) {
            if (elementExtraClass.isInstance(e))
                return (E) e;
        }
        return null;
    }

    public <E extends ElementExtra> E extra(Class<E> elementExtraClass, Supplier<E> supplier) {
        for (ElementExtra e : extras) {
            if (elementExtraClass.isInstance(e))
                return (E) e;
        }
        E e = supplier.get();
        extras.add(e);
        return e;
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    protected @interface OnChange {
        Class<? extends ElementExtra> value();
    }
}
