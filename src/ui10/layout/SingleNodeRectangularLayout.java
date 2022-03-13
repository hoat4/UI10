package ui10.layout;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.geom.Rectangle;
import ui10.geom.Size;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SingleNodeRectangularLayout extends RectangularLayout {

    protected final Element content;

    public SingleNodeRectangularLayout(Element content) {
        this.content = content;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1);

    @Override
    protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
        placer.accept(content, computeContentBounds(size, context));
    }

    protected abstract Rectangle computeContentBounds(Size size, LayoutContext1 context);
}
