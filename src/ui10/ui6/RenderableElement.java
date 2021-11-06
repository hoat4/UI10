package ui10.ui6;

import ui10.binding.PropertyHolder;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public abstract class RenderableElement extends PropertyHolder implements Element {

    public RendererData rendererData;
    public RenderableElement parent;

    protected Shape shape;
    protected List<LayoutContext1.LayoutDependency> layoutDependencies;

    private Element replacement;
    private boolean inReplacement;

    // this should become a manual linked list, standard collections has large memory overhead

    private final Set<Attribute> attributeList = new HashSet<>();

    @Override
    public Set<Attribute> attributes() {
        return attributeList; // onChange
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    public final Shape preferredShape(BoxConstraints constraints, LayoutContext1 context) {
        Objects.requireNonNull(constraints);

        if (replacement == null || inReplacement) {
            Shape s = preferredShapeImpl(constraints, context);
            s = s.translate(s.bounds().topLeft().negate());
            Objects.requireNonNull(s, this::toString);
            context.addLayoutDependency(this, new LayoutContext1.LayoutDependency(constraints, shape));
            return s;
        } else {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                return replacement.preferredShape(constraints, context);
            } finally {
                inReplacement = r;
            }
        }
    }

    protected abstract Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context1);

    @Override
    public final void performLayout(Shape shape, LayoutContext2 context) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(context);

        if (replacement != null && !inReplacement) {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                replacement.performLayout(shape, context);
            } finally {
                inReplacement = r;
            }
            return;
        }

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

    @Override
    public Element replacement() {
        return replacement;
    }

    @Override
    public void replacement(Element e) {
        replacement = e;
        // TODO onChange
    }

    public Shape shape() {
        return shape;
    }

    protected void invalidateRendererData() {
        if (rendererData != null)
            requestLayout();
    }

    void requestLayout() {
        rendererData.invalidateRendererData();
        rendererData.uiContext().requestLayout(new UIContext.LayoutTask(this, ()->{
            final LayoutContext1 consumer = (pane, dep) -> {
                // these known shapes could be used later to avoid computing preferred shapes redundantly
            };

            for (LayoutContext1.LayoutDependency dep : layoutDependencies) {
                if (!preferredShape(dep.inputConstraints(), consumer).equals(dep.shape())) {
                    Objects.requireNonNull(parent, ()->toString());
                    parent.requestLayout();
                    return;
                }
            }

            performLayout(shape, new LayoutContext2.AbstractLayoutContext2() {
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
