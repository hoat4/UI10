package ui10.base;

import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.decoration.css.CSSDecorator;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.util.*;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public non-sealed abstract class RenderableElement extends EnduringElement {

    public RendererData rendererData;

    protected Shape shape;
    protected List<LayoutContext1.LayoutDependency<?, ?>> layoutDependencies;

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = context.getDependencies(this);
        if (changed && rendererData != null)
            rendererData.invalidateRendererData();

        onShapeApplied(shape);
    }

    public void dispatchElementEvent(ElementEvent event) {
        if (initialized || !(event instanceof ChangeEvent<?>)) {
            dispatchPropertyChangeImpl(event);

            CSSDecorator d = decorator();
            if (d != null)
                d.elementEvent(this, event);
        }
    }

    void dispatchPropertyChangeImpl(ElementEvent changeEvent) {
        onPropertyChange(changeEvent);
    }

    protected void onPropertyChange(ElementEvent changeEvent) {
    }

    protected void onShapeApplied(Shape shape) {
    }

    public Shape getShapeOrFail() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    public Point origin() {
        return getShapeOrFail().bounds().topLeft();
    }

    public void invalidate() {
        if (rendererData != null)
            rendererData.invalidateRendererData();
        if (uiContext() == null)
            return;
        // lehet hogy kéne még valami feltételt szabni (pl. van-e már shape)
        uiContext().requestLayout(new UIContext.LayoutTask(this, this::revalidate));
    }

    @Override
    public void invalidateDecoration() {
        invalidate();
    }

    public void revalidate() {
        //System.out.println("revalidate " + this + ": " + shape);

        if (shape == null)
            throw new IllegalStateException(); // should not happen

        LayoutContext1 ctx = new LayoutContext1();

        for (LayoutContext1.LayoutDependency<?, ?> dep : layoutDependencies) {
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

    public static RenderableElement of(Element node) {
        return node instanceof RenderableElement r ? r : Container.of(node);
    }
}
