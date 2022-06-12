package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.di.Component;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public sealed abstract class EnduringElement extends Element implements Component
        permits ElementModel, RenderableElement, RootElement {

    // content of this field should be deleted if child is removed from container, but this is not implemented
    public EnduringElement parent;

    @Override
    public EnduringElement parent() {
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
        EnduringElement e = parent;
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

    @Target(METHOD)
    @Retention(RUNTIME)
    protected @interface OnChange {
        Class<? extends ElementExtra> value();
    }
}
