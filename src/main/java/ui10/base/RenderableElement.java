package ui10.base;

import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public non-sealed abstract class RenderableElement extends Element {

    protected Map<RenderableElement, List<LayoutContext1.LayoutDependency<?, ?>>> layoutDependencies;
    protected Shape shape;

    protected abstract void invalidateRendererData();

    // nevek 4-es layoutban computeSize és setBounds voltak
    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context);

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void applyShape(Shape shape, LayoutContext2 context) {
        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = context.dependencies;
        if (changed)
            invalidateRendererData();

        onShapeApplied(shape);
    }

    @Override
    public RenderableElement renderableElement() {
        return this;
    }

    protected void onShapeApplied(Shape shape) {
    }

    public void invalidate() {
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

        LayoutContext1 ctx = new LayoutContext1(this);

        for (LayoutContext1.LayoutDependency<?, ?> dep : layoutDependencies.getOrDefault(this, Collections.emptyList())) {
            if (ctx.isInvalidated(this, dep)) {
                Objects.requireNonNull(parent, this::toString);
                parentRenderable().invalidate(); // itt parent vagy parentRenderable kell?
                return;
            }
        }

        try {
            onShapeApplied(shape);
        } catch (RuntimeException e) {
            System.err.print("Failed to layout " + this + ": ");
            e.printStackTrace();
        }
    }

    public void initParent(Element parent) {
        this.parent = (Element) parent;

        enumerateStaticChildren(e -> {
            Objects.requireNonNull(e);
            e.initParent(this);
        });
    }

    public Shape getShapeOrFail() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    protected static void performLayoutHelper(LayoutElement e, LayoutContext2 context) {
        e.performLayout(e.getShapeOrFail(), context);
    }

    protected static void enumerateChildrenHelper(LayoutElement e, Consumer<Element> consumer) {
        e.enumerateChildren(consumer);
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point point) {
        return new NullContentPoint(this);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        return shape.bounds().withSize(new Size(0, shape.bounds().height()));
    }

    public static record NullContentPoint(RenderableElement element) implements ContentEditable.ContentPoint {
        @Override
        public int compareTo(ContentEditable.ContentPoint o) {
            assert o.element() == element;
            return 0;
        }
    }
}
