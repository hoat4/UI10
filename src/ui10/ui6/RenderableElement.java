package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.binding.PropertyHolder;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class RenderableElement extends PropertyHolder implements Element {

    public RendererData rendererData;

    protected Shape shape;

    private Element replacement;

    @Override
    public <T extends PropertyEvent> void onChange(T changeEvent) {
        invalidateRendererData();
        super.onChange(changeEvent);
    }

    @Override
    public void enumerateChildren(Consumer<Element> consumer) {
        // all subclasses except Pane have no children
    }

    @Override
    public final Shape preferredShape(BoxConstraints constraints) {
        Objects.requireNonNull(constraints);

        return replacement == null
                ? Objects.requireNonNull(preferredShapeImpl(constraints), this::toString)
                : replacement.preferredShape(constraints);
    }

    protected abstract Shape preferredShapeImpl(BoxConstraints constraints);

    @Override
    public final void applyShape(Shape shape, LayoutContext context) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(context);

        if (replacement != null) {
            replacement.applyShape(shape, context);
            return;
        }

        context.accept(this);

        if (shape.equals(this.shape))
            return;

        this.shape = shape;
        invalidateRendererData(); // vagy inkább onChange()?
        onShapeChanged(shape);
    }

    protected void onShapeChanged(Shape shape) {
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
