package ui10.base;

import ui10.binding2.ChangeEvent;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class Pane extends RenderableElement {

    private final List<RenderableElement> children = new ArrayList<>();
    private boolean valid;

    public FocusContext focusContext;

    protected void validate() {
    }

    @Override
    void dispatchPropertyChange(ChangeEvent changeEvent) {
        onPropertyChange(changeEvent);

        List<RenderableElement> prevChildren = List.copyOf(children);

        if (shape != null && transientDescendantInterestedProperties.contains(changeEvent.property()))
            onShapeApplied(shape);

        for (RenderableElement e : children)
            if (prevChildren.contains(e) && !e.props.containsKey(changeEvent.property())
                    && !e.transientAncestorsProperties.containsKey(changeEvent))
                // subscriptionst is figyelembe kéne venni
                e.dispatchPropertyChange(changeEvent);
    }

    protected void onPropertyChange(ChangeEvent changeEvent) {
    }

    protected abstract Element content();

    private Element getContent() {
        if (!valid) {
            validate();
            valid = true;
        }

        Element content = Objects.requireNonNull(content(), () -> "null content in " + this);
        content.initParent(this);
        return content;
    }

    public List<RenderableElement> renderableElements() {
        if (!valid)
            onShapeApplied(shape);
        return children;
    }

    /**
     * Invalidates layout and content.
     */
    protected void invalidatePane() {
        valid = false;
        invalidate();
    }

    @Override
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(getContent(), constraints);
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        for (RenderableElement child : children)
            child.parent = null;
        children.clear();

        // inter-container layout dependencies are not supported currently
        new LayoutContext2() {

            @Override
            public void accept(RenderableElement e) {
                children.add(e);
                e.parent = Pane.this;
                e.uiContext = uiContext;
                if (e instanceof Pane p)
                    p.focusContext = focusContext;
            }
        }.placeElement(getContent(), shape);
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

                @Override
                public String toString() {
                    return "Pane[" + node + "]";
                }
            };
    }
}
