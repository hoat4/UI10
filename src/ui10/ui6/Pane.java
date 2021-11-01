package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class Pane extends RenderableElement {

    public final List<RenderableElement> children = new ArrayList<>();
    private boolean valid;

    public BiConsumer<Element, Element> decorator;

    protected abstract Element content();

    public final Element getContent() {
        if (!valid) {
            validate();
            valid = true;
        }

        Element content = Objects.requireNonNull(content(), () -> "null content in " + this);
        if (decorator != null)
            decorator.accept(this, content);
        return content;
    }

    protected void validate() {
    }

    @Override
    public <T extends PropertyEvent> void onChange(T changeEvent) {
        super.onChange(changeEvent);
        this.invalidatePane();
    }

    protected void invalidatePane() {
        valid = false;
        if (rendererData != null) {
            rendererData.invalidateRendererData();
        }
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
    }

    @Override
    protected Shape preferredShapeImpl(BoxConstraints constraints) {
        return getContent().preferredShape(constraints);
    }

    @Override
    protected void onShapeApplied(Shape shape, LayoutContext context) {
        children.clear();
        getContent().applyShape(shape, children::add);
    }

    public static Pane of(Element node) {
        if (node instanceof Pane p)
            return p;
        else
            return new Pane() {
                @Override
                public Element content() {
                    return node;
                }
            };
    }
}
