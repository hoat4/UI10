package ui10.base;

import ui10.geom.shape.Shape;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public abstract class RenderableElement extends Element {

    public RendererData rendererData;
    public RenderableElement parent;
    public UIContext uiContext;

    protected Shape shape;
    protected List<LayoutContext1.LayoutDependency> layoutDependencies;

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = context.getDependencies(this);
        if (changed && rendererData != null)
            rendererData.invalidateRendererData();

        onShapeApplied(shape, context);
    }

    protected void onShapeApplied(Shape shape, LayoutContext2 context) {
    }

    public Shape getShapeOrFail() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    public void invalidate() {
        if (rendererData != null)
            rendererData.invalidateRendererData();
        if (uiContext == null)
            return;
        uiContext.requestLayout(new UIContext.LayoutTask(this, this::revalidate));
    }

    private void revalidate() {
        //System.out.println("revalidate " + this + ": " + shape);

        if (shape == null)
            throw new IllegalStateException(); // should not happen

        LayoutContext1 ctx = new LayoutContext1();

        for (LayoutContext1.LayoutDependency dep : layoutDependencies) {
            if (!ctx.preferredSizeIgnoreReplacement(this, dep.inputConstraints()).equals(dep.size())) {
                Objects.requireNonNull(parent, this::toString);
                parent.invalidate();
                return;
            }
        }

        try {
            performLayoutImpl(shape, new LayoutContext2() {
                @Override
                public void accept(RenderableElement element) {
                    // maybe this is an invalid operation?
                    // if only Pane (of RenderableElement subclasses) can have children, then yes
                    throw new UnsupportedOperationException("TODO");
                }

                @Override
                public List<LayoutDependency> getDependencies(RenderableElement element) {
                    if (element == RenderableElement.this)
                        return layoutDependencies;
                    else
                        return super.getDependencies(element);
                }
            });
        } catch (RuntimeException e) {
            System.err.print("Failed to layout " + this + ": ");
            e.printStackTrace();
        }
    }

    public static RenderableElement of(Element node) {
        return node instanceof RenderableElement r ? r : Pane.of(node);
    }
}
