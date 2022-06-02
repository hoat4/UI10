/*
package ui10.base;

import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.decoration.css.CSSDecorator;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Set;
import java.util.function.Consumer;

public non-sealed class ControlModel extends EnduringElement {

    public ControlView<?> view;

    @Override
    protected final void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(view);
    }

    @Override
    public void dispatchElementEvent(ElementEvent event) {
        if (initialized) { // view inicializáltságát nem kéne figyelembe venni?
            if (subscriptions().contains(event.property()))
                view.handleModelEvent(event);
            //for (ExternalListener<?> el : externalListeners)
            //    elHelper(el, event);

            // descendantokat is értesítsük?
        }

        if (initialized || !(event instanceof ChangeEvent<?>)) {
//            CSSDecorator d = decorator();
//            if (d != null)
//                d.elementEvent(this, event);
        }
    }

    // ez így nem jó, mert alternatív layout protokolloknál dobozosít
    @Override
    protected final Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(view, constraints);
    }

    @Override
    protected final void performLayoutImpl(Shape shape, LayoutContext2 context) {
        context.placeElement(view, shape);
    }

    @Override
    protected Set<Property<?>> subscriptions() {
        return view.modelPropertySubscriptions();
    }

    @Override
    public void invalidateDecoration() {
        view.invalidate();
    }
}
*/