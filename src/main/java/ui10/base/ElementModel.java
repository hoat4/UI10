package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.binding5.ListenerMulticaster;
import ui10.binding5.Parameterization;
import ui10.binding5.ReflectionUtil;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public non-sealed class ElementModel<L extends ElementModel.ElementModelListener> extends EnduringElement {

    L listener;

    @Override
    // TODO legyen ez package-private
    protected void initLogicalParent(Element logicalParent) {
        super.initLogicalParent(logicalParent);
        setView((L) uiContext().viewProvider().makeView(this));
    }

    /**
     * @param view must be instance of {@linkplain EnduringElement},
     *             but there's no way to express this in the type system
     */
    public void setView(L view) {
        assert view instanceof EnduringElement;
        listener = view;
    }

    @Override
    protected final void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(view());
    }

    // overriden in DecoratedControlModel
    // ez így nem jó, hogy dekorált elementmodeleken is látszik ez a függvény
    public Element view() {
        if (listener == null)
            throw new IllegalStateException("no view associated with "+this+" (parent: "+parent+")");
        return (Element)listener;
    }

    @Override
    public void dispatchElementEvent(ElementEvent event) {
    }

    // ez így nem jó, mert alternatív layout protokolloknál dobozosít
    @Override
    protected final Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(view(), constraints);
    }

    @Override
    protected final void performLayoutImpl(Shape shape, LayoutContext2 context) {
        context.placeElement(view(), shape);
    }

    @Override
    public void invalidateDecoration() {
        // view().invalidateDecoration();
    }

    protected L listener() {
        List<L> list = listener == null ? List.of() : List.of(listener);
        return ListenerMulticaster.makeMulticaster(listenerClass(), list);
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
