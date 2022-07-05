package ui10.base;

import ui10.binding5.ReflectionUtil;
import ui10.binding9.Observer2;
import ui10.di.Component;
import ui10.geom.Point;
import ui10.geom.shape.Shape;
import ui10.input.Event;
import ui10.input.Phase;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
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
    public Component depParent;

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
        collect(clazz, o -> {
            Objects.requireNonNull(o);
            list.add(o);
        });
        return list;
    }

    protected final Point relativePos(Element e) { // lehetne publikus is, pl. JavaFX-ben publikus
        return e.origin().subtract(origin());
    }

    @Override
    public <T> void collect(Class<T> type, Consumer<T> consumer) {
        if (parent != null)
            parent.collect(type, consumer);
        if (depParent != null)
            depParent.collect(type, consumer);
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

    public abstract ContentEditable.ContentPoint pickPosition(Point point);

    public abstract Shape shapeOfSelection(ContentEditable.ContentRange<?> range);

    @SuppressWarnings("unchecked")
    public <R extends Event.EventResponse> R handleEvent(Event<R> event, Phase phase) {
        List<Method> methods = phase == Phase.BUBBLE
                ? ReflectionUtil.methodsIn(getClass()) : ReflectionUtil.methodsIn2(getClass());


        for (Method m : methods) {
            EventHandler h = m.getAnnotation(EventHandler.class);
            if (h != null && h.phase() == phase) {
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes[0].isAssignableFrom(event.getClass())) {
                    m.setAccessible(true);

                    R response;
                    try {
                        response = (R) m.invoke(this, event);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("can't invoke event handler " +
                                m.getDeclaringClass().getSimpleName() + "." + m.getName() + ": " + e, e);
                    }

                    if (response != null)
                        return response;
                }
            }
        }
        return null;
    }

    public <E extends ElementExtra> E extra(Class<E> elementExtraClass) {
        for (ElementExtra e : extras) {
            if (elementExtraClass.isInstance(e))
                return elementExtraClass.cast(e);
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

    @Target(METHOD)
    @Retention(RUNTIME)
    protected @interface EventHandler {

        Phase phase() default Phase.BUBBLE;
    }

    protected abstract class DelayedObserver extends Observer2 {

        private boolean valid = true;

        @Override
        protected void invalidate() {
            if (!valid)
                return;

            valid = false;
            lookup(UIContext.class).eventLoop().runLater(() -> {
                valid = true;
                invalidateImpl();
            });
        }

        protected abstract void invalidateImpl();
    }

    //private static void dispatchInputEvent(Event event, InputHandler ih, EventContext context, boolean capture) {
    // reportolni kéne, hogy félrevezető az Stream::iterate-ben a hasNext elnevezése

    //}
}
