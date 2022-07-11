package ui10.base;

import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Objects;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public abstract class RenderableElement extends Element {

    protected abstract void invalidateRendererData();

    // nevek 4-es layoutban computeSize és setBounds voltak
    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context);

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void shapeChanged() {
        invalidateRendererData();
    }

    @Override
    public RenderableElement renderableElement() {
        return this;
    }

    @Override
    protected void applyShape(Shape shape, LayoutContext2 context) {
        super.applyShape(shape, context);
        onShapeApplied(shape);
    }

    protected void onShapeApplied(Shape shape) {
    }

    public void invalidateRenderableElementAndLayout() {
        invalidateRendererData();
        if (lookup(UIContext.class) == null)
            return;

        if (shape == null)
            return;

        lookup(UIContext.class).requestLayout(new UIContext.LayoutTask(this, this::revalidate));
    }

    public void revalidate() {
        //System.out.println("revalidate " + this + ": " + shape);

        if (shape == null)
            throw new IllegalStateException("no shape for " + this); // should not happen because of the check in invalidate()

        try {
            onShapeApplied(shape);
        } catch (RuntimeException e) {
            System.err.print("Failed to layout " + this + ": ");
            e.printStackTrace();
        }
    }

    @Override
    void initView() {
        enumerateStaticChildren(e -> {
            Objects.requireNonNull(e,()->this.toString());
            e.initParent(this);
        });
    }

    public Shape shape() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    @Override
    public Element view() {
        return null;
    }

    protected static void performLayoutHelper(LayoutElement e, LayoutContext2 context) {
        e.performLayout(e.shape(), context);
    }

    protected static void enumerateChildrenHelper(LayoutElement e, Consumer<Element> consumer) {
        e.enumerateChildren(consumer);
    }
}
