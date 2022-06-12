package ui10.base;

import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public sealed abstract class Element permits TransientElement, EnduringElement  {

    public final List<ElementExtra> extras = new ArrayList<>();

    // nevek 4-es layoutban computeSize és setBounds voltak
    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context);

    protected abstract void performLayoutImpl(Shape shape, LayoutContext2 context);

    /**
     * This can be used by decorators to walk the elementClass tree. When encountering a Pane, the decorator should
     * set the Pane.decorator field because Panes usually recreate its children every time, so decorating them only once
     * is useless.
     */
    protected abstract void enumerateStaticChildren(Consumer<Element> consumer); // this does not honor replacement

    public abstract void initParent(Element parent);

    public abstract Element parent();


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
}
