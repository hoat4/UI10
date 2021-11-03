package ui10.ui6;

import ui10.binding.PropertyHolder;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutResult;

import java.util.*;
import java.util.function.Consumer;

public abstract class RenderableElement extends PropertyHolder implements Element {

    public RendererData rendererData;

    protected Shape shape;
    protected List<LayoutResult> layoutDependencies;

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
    public final LayoutResult preferredShape(BoxConstraints constraints) {
        Objects.requireNonNull(constraints);

        if (replacement == null || inReplacement) {
            LayoutResult s = preferredShapeImpl(constraints);
            Objects.requireNonNull(s, this::toString);
            assert s.elementClass() == getClass() : this + ", " + s;
            return s;
        } else {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                return replacement.preferredShape(constraints);
            } finally {
                inReplacement = r;
            }
        }
    }

    protected abstract LayoutResult preferredShapeImpl(BoxConstraints constraints);

    @Override
    public final void performLayout(Shape shape, LayoutContext context, List<LayoutResult> lr) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(context);

        if (replacement != null && !inReplacement) {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                replacement.performLayout(shape, context, lr);
            } finally {
                inReplacement = r;
            }
            return;
        }

        for (LayoutResult lh : lr)
            assert lh.elementClass() == getClass();

        context.accept(this);

        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = lr;
        if (changed && rendererData != null)
            rendererData.invalidateRendererData();

        onShapeApplied(shape, context, lr);
    }

    protected void onShapeApplied(Shape shape, LayoutContext context, List<LayoutResult> dependencies) {
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
            rendererData.invalidateRendererData();
    }

    public static RenderableElement of(Element node) {
        return node instanceof RenderableElement r ? r : Pane.of(node);
    }
}
