package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Pane extends Surface {

    public final List<Surface> children = new ArrayList<>();
    public EventHandler eventHandler;
    private boolean valid;

    public abstract Element content();

    @Override
    public Shape computeShape(BoxConstraints constraints) {
        return getContent().computeShape(constraints);
    }

    private Element getContent() {
        if (!valid) {
            validate();
            valid = true;
        }

        Element c = content();
        Objects.requireNonNull(c, () -> "null content in " + toString());
        return c;
    }

    protected void validate() {
    }

    @Override
    public <T extends PropertyEvent> void onChange(T changeEvent) {
        valid = false;
        super.onChange(changeEvent);
    }

    @Override
    protected void applyShapeImpl(Shape shape, Consumer<Surface> layoutContext) {
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
