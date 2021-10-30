package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Pane extends RenderableElement {

    public final List<RenderableElement> children = new ArrayList<>();
    private boolean valid;

    protected abstract Element content();

    public final Element getContent() {
        if (!valid) {
            validate();
            valid = true;
        }

        return Objects.requireNonNull(content(), () -> "null content in " + this);
    }

    protected void validate() {
    }

    @Override
    public <T extends PropertyEvent> void onChange(T changeEvent) {
        valid = false;
        super.onChange(changeEvent);
    }

    @Override
    public void enumerateChildren(Consumer<Element> consumer) {
        consumer.accept(getContent());
    }

    @Override
    protected Shape preferredShapeImpl(BoxConstraints constraints) {
        return getContent().preferredShape(constraints);
    }

    @Override
    protected void onShapeChanged(Shape shape) {
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
