package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.binding.PropertyHolder;
import ui10.geom.shape.Shape;

import java.util.function.Consumer;

public abstract class Surface extends PropertyHolder implements Element {

    public RendererData rendererData;
    private Shape shape;

    @Override
    public <T extends PropertyEvent> void onChange(T changeEvent) {
        invalidate();
        super.onChange(changeEvent);
    }

    @Override
    public final void applyShape(Shape shape, Consumer<Surface> consumer) {
        if (shape.equals(this.shape))
            return;

        consumer.accept(this);

        this.shape = shape;
        invalidate(); // vagy inkább onChange()?
        applyShapeImpl(shape, consumer);
    }

    protected void applyShapeImpl(Shape shape, Consumer<Surface> layoutContext) {
    }

    public Shape shape() {
        return shape;
    }

    protected void invalidate() {
        if (rendererData != null)
            rendererData.invalidateRendererData();
    }

    public static Surface of(Element node) {
        if (node instanceof Surface r)
            return r;
        else
            return Pane.of(node);
    }
}
