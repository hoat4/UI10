package ui10.base;

import ui10.binding5.ReflectionUtil;
import ui10.binding9.OVal;
import ui10.binding9.Observer2;
import ui10.di.Component;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.input.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

public abstract class Element implements Component {

    public final List<ElementExtra> extras = new ArrayList<>();
    // content of this field should be deleted if child is removed from container, but this is not implemented
    public Element parent;
    public Component depParent;

    final OVal<Element> next = new OVal<>() {
        @Override
        protected void afterChange(Element oldValue, Element newValue) {
            if (newValue != null && Element.this.parent != null)
                newValue.initParent(Element.this);
        }
    };
    Shape shape;
    boolean nextInit;

    private final Set<MethodInvalidationPoint> dirtySet = new HashSet<>();

    public Element parent() {
        return parent;
    }

    public <T> T lookup(Class<T> clazz) {
        List<T> list = lookupMultiple(clazz);
        if (list.isEmpty())
            throw new RuntimeException("unknown context type: " + clazz);

        // TODO több UIContext-et talált dialógusból hívva. mi legyen ezzel?
        // if (list.size() > 1)
        //    throw new RuntimeException("multiple values found for context type " + clazz.getName() + ": " + list);

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
        Element n = next.get();
        if (n != null)
            return n.hasShape();
        return shape != null;
    }

    public Shape shape() {
        checkInitialized();
        Element n = next.get();
        if (n != null)
            return n.shape();
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    public boolean isRenderableElement() {
        checkInitialized();
        return next.get() == null;
    }

    public Element renderableElement() {
        checkInitialized();
        return next.get() == null ? this : next.get().renderableElement();
    }

    protected void preShapeChange(Shape shape) {
    }

    protected void applyShape(Shape shape, LayoutContext2 context) {
        checkInitialized(); // ezt a kettőt lehet hogy fel kéne cseréni
        preShapeChange(shape);

        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        if (changed)
            shapeChanged();

        if (next.get() != null)
            context.placeElement(next.get(), shape);
    }

    protected void shapeChanged() {
    }

    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        if (view() != null)
            consumer.accept(view());
    }

    public void initParent(Element parent) {
        Objects.requireNonNull(parent);

        Element e = parent;
        if (e == this.parent)
            return;

        // ennek hosszabb távon lehet hogy nem exceptionnek kéne lennie, hanem talán csak egy warningnak
        // vagy ki kéne válogatni hogy mely esetekben legyen exception
        if (this.parent != null)
            throw new IllegalStateException(this + " already has parent (old parent: " + this.parent
                    + ", new parent: " + parent + ", next: " + next.snoop());

        this.parent = e;

        initBeforeView();

        if (!nextInit) {
            initView();
            nextInit = true;
        }
    }

    void initView() {
        next.set(ViewProvider.makeView(this, lookupMultiple(ViewProvider.class)));
    }

    protected void initBeforeView() {
        for (Initializer initializer : Initializer.CV.get(getClass()))
            initializer.execute(this);
    }

    private static abstract class Initializer {

        final Method m;
        private final int stage;

        public Initializer(Method m, int stage) {
            this.m = m;
            this.stage = stage;
        }

        static final ClassValue<List<Initializer>> CV = new ClassValue<>() {
            @Override
            protected List<Initializer> computeValue(Class<?> type) {
                return ReflectionUtil.methodsIn2(type).stream().
                        map(m -> {
                            OneTimeInit ann1 = m.getAnnotation(OneTimeInit.class);
                            if (ann1 != null)
                                return new OneTimeInitializer(m, ann1);
                            RepeatedInit ann2 = m.getAnnotation(RepeatedInit.class);
                            if (ann2 != null)
                                return new RepeatedInitializer(m, ann2);
                            return null;
                        }).
                        filter(Objects::nonNull).
                        sorted(comparing(initializer -> initializer.stage)).
                        toList();
            }
        };

        abstract void execute(Element element);

        static class OneTimeInitializer extends Initializer{

            public OneTimeInitializer(Method m, OneTimeInit ann) {
                super(m, ann.value());
            }

            @Override
            void execute(Element element) {
                ReflectionUtil.invokeMethod(m, element);
            }
        }

        static class RepeatedInitializer extends Initializer{

            public RepeatedInitializer(Method m, RepeatedInit ann) {
                super(m, ann.value());
            }

            @Override
            void execute(Element element) {
                element.new MethodInvalidationPoint(m).executeObserved(() -> ReflectionUtil.invokeMethod(m, element));
            }
        }
    }

    private void revalidate() {
        Set<MethodInvalidationPoint> dirtySetCopy = new HashSet<>(dirtySet);
        dirtySet.clear();
        for (MethodInvalidationPoint p : dirtySetCopy) {
            p.executeObserved(() -> ReflectionUtil.invokeMethod(p.method, this));
        }
    }

    private class MethodInvalidationPoint extends Observer2 {

        final Method method;

        public MethodInvalidationPoint(Method method) {
            this.method = method;
        }

        @Override
        protected void invalidate() {
            boolean wasEmpty = dirtySet.isEmpty();
            dirtySet.add(this);
            if (wasEmpty && parent != null)
                lookup(UIContext.class).eventLoop().runLater(Element.this::revalidate);
        }
    }

    // TODO mi van ha változik a view?
    public Element view() {
        if (!nextInit)
            throw new IllegalStateException("no view associated with " + this + " (parent: " + parent + ")");
        return next.get();
    }

    public ContentEditable.ContentPoint pickPosition(Point point) {
        checkInitialized();
        if (next.get() == null)
            return new NullContentPoint(this);
        else
            return next.get().pickPosition(point);
    }

    protected void checkInitialized() {
        if (!nextInit)
            throw new IllegalStateException(this + " is not initialized yet");
    }

    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        checkInitialized();
        if (next.get() == null)
            return shape().bounds().withSize(new Size(0, shape().bounds().height()));
        else
            return view().shapeOfSelection(range);
    }

    @SuppressWarnings("unchecked")
    public final <R extends EventInterpretation.EventResponse> EventResultWrapper<R> dispatchEvent(
            EventInterpretation<R> event) {
        Event e = new Event(event);
        return (EventResultWrapper<R>) dispatchEvent(e);
    }

    public final EventResultWrapper<?> dispatchEvent(Event event) {
        return doDispatchEvent(event, new ArrayList<>());
    }

    private EventResultWrapper<? extends EventInterpretation.EventResponse> doDispatchEvent(Event event,
                                                                                            List<EventInterpretation<?>> applyingEventInterpretations) {

        List<AdditionalInterpretation<?, ?>> additionalInterpretations = additionalInterpretations(event);
        int l = event.interpretations.size();
        event.interpretations.addAll(additionalInterpretations.stream().map(ai -> ai.dst).collect(Collectors.toList()));

        EventResultWrapper<?> result = dispatchEventImpl(event, applyingEventInterpretations);
        for (int i = event.interpretations.size() - 1; i >= l; i--)
            event.interpretations.remove(i);


        additionalInterpretations.forEach(ai -> {
            if (applyingEventInterpretations.remove(ai.dst) && !applyingEventInterpretations.contains(ai.src))
                applyingEventInterpretations.add(ai.src);
        });

        if (result != null && !event.interpretations().contains(result.eventInterpretation())) {
            for (AdditionalInterpretation<?, ?> ai : additionalInterpretations) {
                EventResultWrapper<?> convertedResult = tryConvertResultBack(result, ai);

                // TODO itt a null kezelés nincs átgondolva
                if (convertedResult != null)
                    return convertedResult;
            }
            throw new RuntimeException();
        }

        return result;
    }

    private <R1 extends EventInterpretation.EventResponse, R2 extends EventInterpretation.EventResponse>
    EventResultWrapper<R1> tryConvertResultBack(
            EventResultWrapper<R2> result, AdditionalInterpretation<?, ?> ai) {

        if (ai.dst.equals(result.eventInterpretation())) {
            @SuppressWarnings("unchecked")
            AdditionalInterpretation<R1, R2> ai2 = (AdditionalInterpretation<R1, R2>) ai;

            return new EventResultWrapper<>(result.responder(), ai2.src, ai2.responseConverter.apply(result.response()));
        }
        return null;
    }

    private EventResultWrapper<? extends EventInterpretation.EventResponse> dispatchEventImpl(Event event,
                                                                                              List<EventInterpretation<?>> applyingInterpretations) {
        EventResultWrapper<?>[] b = new EventResultWrapper[1];
        enumerateStaticChildren(e -> {
            List<EventInterpretation<?>> appInts3 = new ArrayList<>();
            if (b[0] == null)
                b[0] = e.doDispatchEvent(event, appInts3);
            for (EventInterpretation<?> i : appInts3)
                if (!applyingInterpretations.contains(i))
                    applyingInterpretations.add(i);
        });

        if (b[0] != null)
            return b[0];

        for (EventInterpretation<?> i : event.interpretations()) {
            Element elem = this;
            while (elem != null) {
                boolean applies = applyingInterpretations.contains(i);
                if (!applies && i.target().appliesTo(this)) {
                    applies = true;
                    applyingInterpretations.add(i);
                }
                if (applies) {
                    EventResultWrapper<?> r = handleEventInterpretation(i);
                    if (r != null)
                        return r;
                    break;
                }
                elem = elem.parent;
            }
        }
        return null;
    }

    protected List<AdditionalInterpretation<?, ?>> additionalInterpretations(Event event) {
        return emptyList();
    }

    protected record AdditionalInterpretation<R1 extends EventInterpretation.EventResponse,
            R2 extends EventInterpretation.EventResponse>(
            EventInterpretation<R1> src, EventInterpretation<R2> dst, Function<R2, R1> responseConverter) {
    }

    @SuppressWarnings("unchecked")
    private <R extends EventInterpretation.EventResponse> EventResultWrapper<R> handleEventInterpretation(
            EventInterpretation<R> event) {

        List<Method> methods = ReflectionUtil.methodsIn(getClass());

        for (Method m : methods) {
            EventHandler h = m.getAnnotation(EventHandler.class);
            if (h != null) {
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes[0].isAssignableFrom(event.getClass())) {
                    m.setAccessible(true);

                    R response;
                    try {
                        response = (R) m.invoke(this, event);
                    } catch (ReflectiveOperationException e) {
                        if (e.getCause() instanceof Error error)
                            throw error;
                        if (e.getCause() instanceof RuntimeException re)
                            throw re;
                        throw new RuntimeException("can't invoke event handler " +
                                m.getDeclaringClass().getSimpleName() + "." + m.getName() + ": " + e, e);
                    }

                    if (response != null)
                        return new EventResultWrapper<>(this, event, response);
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

    public List<Element> ancestors(Element e) {
        List<Element> l = new ArrayList<>();
        while (e != this.parent) {
            l.add(e);
            e = e.parent;
        }
        Collections.reverse(l);
        return l;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toUnsignedString(hashCode(), 16);
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    protected @interface OneTimeInit {

        int value() default 0;
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    protected @interface RepeatedInit {

        int value() default 0;
    }
}
