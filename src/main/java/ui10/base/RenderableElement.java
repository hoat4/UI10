package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.geom.shape.Shape;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public non-sealed abstract class RenderableElement extends EnduringElement {

    protected Shape shape;
    protected Map<RenderableElement, List<LayoutContext1.LayoutDependency<?, ?>>> layoutDependencies;

    protected abstract void invalidateRendererData();

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = context.dependencies;
        if (changed)
            invalidateRendererData();

        onShapeApplied(shape);
    }

    @Override
    public void initParent(Element parent) {
        Element e = parent;
        while (e instanceof TransientElement t) {
            e = t.logicalParent;
        }

        this.parent = (EnduringElement) e;

        enumerateStaticChildren(elem -> elem.initParent(this));
    }

    @Override
    public RenderableElement renderableElement() {
        return this;
    }

    protected void onShapeApplied(Shape shape) {
    }

    public Shape getShapeOrFail() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    public void invalidate() {
        invalidateRendererData();
        if (lookup(UIContext.class) == null)
            return;
        // lehet hogy kéne még valami feltételt szabni (pl. van-e már shape)
        lookup(UIContext.class).requestLayout(new UIContext.LayoutTask(this, this::revalidate));
    }

    public void revalidate() {
        //System.out.println("revalidate " + this + ": " + shape);

        if (shape == null)
            throw new IllegalStateException(); // should not happen

        LayoutContext1 ctx = new LayoutContext1();

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
}
