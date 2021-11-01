package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.binding.PropertyHolder;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class RenderableElement extends PropertyHolder implements Element {

    public RendererData rendererData;

    protected Shape shape;

    private Element replacement;
    private boolean inReplacement;

    // this should become a manual linked list, standard collections has large memory overhead

    private final List<Attribute> attributeList = new ArrayList<>();

    @Override
    public List<Attribute> attributes() {
        return attributeList; // onChange
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    public final Shape preferredShape(BoxConstraints constraints) {
        Objects.requireNonNull(constraints);

        if (replacement == null || inReplacement) {
            Shape s = preferredShapeImpl(constraints);
            Objects.requireNonNull(s, this::toString);
            s = s.translate(s.bounds().topLeft().negate());
            Objects.requireNonNull(s);
            return s;
        }else {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                return replacement.preferredShape(constraints);
            } finally {
                inReplacement = r;
            }
        }
    }

    protected abstract Shape preferredShapeImpl(BoxConstraints constraints);

    @Override
    public final void applyShape(Shape shape, LayoutContext context) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(context);

        if (replacement != null && !inReplacement) {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                replacement.applyShape(shape, context);
            } finally {
                inReplacement = r;
            }
            return;
        }

        context.accept(this);

        this.shape = shape;
        onShapeApplied(shape, context);
    }

    protected void onShapeApplied(Shape shape, LayoutContext context) {
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
