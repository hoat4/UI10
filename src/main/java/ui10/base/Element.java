package ui10.base;

import ui10.binding5.ReflectionUtil;
import ui10.binding9.Observer2;
import ui10.di.Component;
import ui10.geom.Point;
import ui10.geom.Size;
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

public abstract class Element implements Component {

    public final List<ElementExtra> extras = new ArrayList<>();
    // content of this field should be deleted if child is removed from container, but this is not implemented
    public Element parent;
    public Component depParent;

    Element next;
    Shape shape;
    boolean nextInit;

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

    public Element parentRenderable() {
        Element e = parent;
        while (e != null && !e.isRenderableElement())
            e = e.parent;
        return e;
    }

    public Point origin() {
        return shape().bounds().topLeft();
    }

    public boolean hasShape() {
        return shape != null;
    }

    public Shape shape() {
        ensureViewInit();
        if (next != null)
            return next.shape();
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    public boolean isRenderableElement() {
        ensureViewInit();
        return next == null;
    }

    public Element renderableElement() {
        ensureViewInit();
        return next == null ? this : next.renderableElement();
    }

    protected void preShapeChange(Shape shape) {
    }

    protected void applyShape(Shape shape, LayoutContext2 context) {
        ensureViewInit(); // ezt a kettőt lehet hogy fel kéne cseréni
        preShapeChange(shape);

        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        if (changed)
            shapeChanged();

        if (next != null)
            context.placeElement(next, shape);
    }

    protected void shapeChanged() {
    }

    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        if (view() != null)
            view().enumerateStaticChildren(consumer);
    }

    public void initParent(Element parent) {
        Element e = parent;
        if (e == this.parent)
            return;

        if (nextInit)
            throw new IllegalStateException(this + " already has view: " + next + " (old parent: " + this.parent + ", new parent: " + parent + ")");

        this.parent = e;

        // TODO ez a predicate nem jó ha csak megváltoztattuk a parentet
        ReflectionUtil.invokeAnnotatedMethods(this, Element.OnChange.class,
                ann -> !lookupMultiple(ann.value()).isEmpty());

        initBeforeView();

        initView();
        nextInit = true;
    }

    void initView() {
        next = ViewProvider.makeView(this, lookupMultiple(ViewProvider.class));
        if (next != null)
            next.initParent(this);
    }

    protected void initBeforeView() {
    }

    // TODO mi van ha változik a view?
    public Element view() {
        if (!nextInit)
            throw new IllegalStateException("no view associated with " + this + " (parent: " + parent + ")");
        return next;
    }

    public ContentEditable.ContentPoint pickPosition(Point point) {
        ensureViewInit();
        if (next == null)
            return new NullContentPoint(this);
        else
            return next.pickPosition(point);
    }

    private void ensureViewInit() {
        if (!nextInit)
            throw new IllegalStateException(this + " is not initialized yet");
    }

    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        ensureViewInit();
        if (next == null)
            return shape().bounds().withSize(new Size(0, shape().bounds().height()));
        else
            return view().shapeOfSelection(range);
    }

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


    public static record NullContentPoint(Element element) implements ContentEditable.ContentPoint {
        @Override
        public int compareTo(ContentEditable.ContentPoint o) {
            assert o.element() == element;
            return 0;
        }
    }
}
