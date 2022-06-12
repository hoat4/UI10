package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.binding5.ListenerMulticaster;
import ui10.binding5.Parameterization;
import ui10.binding5.ReflectionUtil;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public non-sealed class ElementModel<L extends ElementModel.ElementModelListener> extends EnduringElement {

    private final List<L> listeners = new ArrayList<>();

    public void initParent(Element parent) {
        Element e = parent;
        while (e instanceof TransientElement t)
            e = t.logicalParent;
        if (e == this.parent)
            return;
        this.parent = (EnduringElement) e;

        if (!listeners.isEmpty())
            throw new IllegalStateException();

        // view must be instance of {@linkplain EnduringElement},
        // but there's no way to express this in the type system
        L view = (L) ViewProvider.makeView(this, lookupMultiple(ViewProvider.class));
        assert view instanceof EnduringElement;
        listeners.add(view);
        ((EnduringElement) view).initParent(this);

        // TODO ez a predicate nem jó ha csak megváltoztattuk a parentet
        ReflectionUtil.invokeAnnotatedMethods(this, EnduringElement.OnChange.class,
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
    public EnduringElement view() {
        if (listeners.isEmpty())
            throw new IllegalStateException("no view associated with " + this + " (parent: " + parent + ")");
        return (EnduringElement) listeners.get(0);
    }

    public Shape getShapeOrFail() {
        return view().getShapeOrFail();
    }

    // ezt így értelmetlen használni, mert alternatív layout protokolloknál dobozosít
    // nem is használjuk, LayoutContext1::preferredSize kikerüli
    @Override
    protected final Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(view(), constraints);
    }

    protected void preShapeChange(Shape shape) {
    }

    @Override
    protected final void performLayoutImpl(Shape shape, LayoutContext2 context) {
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
        // TODO itt NPE lesz, ha van a végső leszármazottnak type variable-je
        return (Class<L>) ReflectionUtil.rawType(
                Parameterization.ofRawType(getClass()).resolve(ElementModel.class.getTypeParameters()[0]));
    }

    public interface ElementModelListener {
    }
}
