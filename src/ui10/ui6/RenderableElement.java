package ui10.ui6;

import ui10.binding.PropertyHolder;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public abstract class RenderableElement extends Element {

    public RendererData rendererData;
    public RenderableElement parent;

    protected Shape shape;
    protected List<LayoutContext1.LayoutDependency> layoutDependencies;

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        context.accept(this);

        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = context.getDependencies(this);
        if (changed && rendererData != null)
            rendererData.invalidateRendererData();

        onShapeApplied(shape, context);
    }

    protected void onShapeApplied(Shape shape, LayoutContext2 context) {
    }

    public Shape shapeOrFail() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    protected void invalidateRendererData() {
        if (rendererData != null)
            requestLayout();
    }

    public void requestLayout() {
        rendererData.invalidateRendererData();
        rendererData.uiContext().requestLayout(new UIContext.LayoutTask(this, () -> {
            LayoutContext1 consumer = new LayoutContext1() {
                @Override
                public RenderableElement lowestRenderableElement() {
                    return null;
                }

                @Override
                public void addLayoutDependency(RenderableElement element, LayoutDependency d) {
                    // these known shapes should be used later to avoid computing preferred shapes redundantly
                }
            };

            for (LayoutContext1.LayoutDependency dep : layoutDependencies) {
                if (!preferredSize(dep.inputConstraints(), consumer).equals(dep.shape())) {
                    Objects.requireNonNull(parent, this::toString);
                    parent.requestLayout();
                    return;
                }
            }

            performLayout(shape, new LayoutContext2.AbstractLayoutContext2(null) {
                @Override
                public void accept(RenderableElement element) {
                }

                @Override
                public List<LayoutDependency> getDependencies(RenderableElement element) {
                    if (element == RenderableElement.this)
                        return layoutDependencies;
                    else
                        return super.getDependencies(element);
                }
            });
        }));
    }

    public static RenderableElement of(Element node) {
        return node instanceof RenderableElement r ? r : Pane.of(node);
    }
}
