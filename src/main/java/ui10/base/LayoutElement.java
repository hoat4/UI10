package ui10.base;

import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

import static ui10.binding9.Bindings.repeatIfInvalidated;

public abstract class LayoutElement extends Element {

    protected abstract void enumerateChildren(Consumer<Element> consumer);

    protected abstract Size preferredSize(BoxConstraints constraints, LayoutContext1 context1);

    protected abstract void performLayout(Shape shape, LayoutContext2 context1);

    public static void performLayoutHelper(LayoutElement e, Shape shape, LayoutContext2 context) {
        e.performLayout(shape, context);
    }

    @Override
    void initView() {
        super.initView();
        repeatIfInvalidated(() -> {
            if (next.get() == null)
                enumerateChildren(e -> {
                    if (e == null)
                        throw new RuntimeException("enumerateChildren gave null element in: " + this);
                    e.initParent(this);
                });
        });
    }
}
