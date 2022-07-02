package ui10.base;

import ui10.binding5.ListenerMulticaster;
import ui10.binding5.Parameterization;
import ui10.binding5.ReflectionUtil;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public non-sealed abstract class ElementModel<L extends ElementModel.ElementModelListener> extends Element {

    private final List<L> listeners = new ArrayList<>();

    public void initParent(Element parent) {
        Element e = parent;
        if (e == this.parent)
            return;

        if (!listeners.isEmpty())
            throw new IllegalStateException(this + " already has listeners: " + listeners + " (old parent: " + this.parent + ", new parent: " + parent + ")");

        this.parent = e;

        // view must be instance of {@linkplain EnduringElement},
        // but there's no way to express this in the type system
        L view = (L) ViewProvider.makeView(this, lookupMultiple(ViewProvider.class));
        assert view instanceof Element;
        listeners.add(view);
        ((Element) view).initParent(this);

        // TODO ez a predicate nem jó ha csak megváltoztattuk a parentet
        ReflectionUtil.invokeAnnotatedMethods(this, Element.OnChange.class,
                ann -> !lookupMultiple(ann.value()).isEmpty());
    }

    @Override
    protected final void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(view());
    }

    // TODO mi van ha változik a view?
    @Override
    public RenderableElement renderableElement() {
        return view().renderableElement();
    }

    // ez így nem jó, hogy dekorált elementmodeleken is látszik ez a függvény
    public Element view() {
        if (listeners.isEmpty())
            throw new IllegalStateException("no view associated with " + this + " (parent: " + parent + ")");
        return (Element) listeners.get(0);
    }

    public Shape getShapeOrFail() {
        return view().getShapeOrFail();
    }

    protected void preShapeChange(Shape shape) {
    }

    @Override
    protected final void applyShape(Shape shape, LayoutContext2 context) {
        preShapeChange(shape);
        context.placeElement(view(), shape);
    }

    protected L listener() {
        return ListenerMulticaster.makeMulticaster(listenerClass(), listeners);
    }

    // ennek nem kéne nyilvánosan bejárhatónak lennie
    public List<L> listeners() {
        return listeners;
    }

    @SuppressWarnings("unchecked")
    private Class<L> listenerClass() {
        return (Class<L>) LISTENER_CLASS_CV.get(getClass());
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point point) {
        return view().pickPosition(point);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        return view().shapeOfSelection(range);
    }

    public interface ElementModelListener {
    }

    private static final ClassValue<Class<? extends ElementModelListener>> LISTENER_CLASS_CV = new ClassValue<Class<? extends ElementModelListener>>() {
        @SuppressWarnings("unchecked")
        @Override
        protected Class<? extends ElementModelListener> computeValue(Class<?> type) {
            return (Class<? extends ElementModelListener>) ReflectionUtil.rawType(
                    Parameterization.ofRawType(type).resolve(ElementModel.class.getTypeParameters()[0]));
        }
    };
}
