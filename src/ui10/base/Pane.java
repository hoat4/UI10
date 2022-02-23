package ui10.base;

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

    public BiConsumer<Pane, Element> decorator;
    public FocusContext focusContext;

    protected void validate() {
    }

    protected abstract Element content();

    private Element getContent() {
        if (!valid) {
            validate();
            valid = true;
        }

        Element content = Objects.requireNonNull(content(), () -> "null content in " + this);
        if (decorator != null)
            decorator.accept(this, content);
        return content;
    }

    public List<RenderableElement> renderableElements() {
        if (!valid)
            onShapeApplied(shape, new LayoutContext2() {
                @Override
                public void accept(RenderableElement element) {
                    throw new UnsupportedOperationException();
                }
            });
        return children;
    }

    /**
     * Invalidates layout and content.
     */
    protected void invalidatePane() {
        valid = false;
        requestLayout();
    }

    @Override
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(getContent(), constraints);
    }

    @Override
    protected void onShapeApplied(Shape shape, LayoutContext2 context) {
        for (RenderableElement child : children)
            child.parent = null;
        children.clear();

        new LayoutContext2() {
            @Override
            public void accept(RenderableElement e) {
                children.add(e);
                e.parent = Pane.this;
                if (e instanceof Pane p)
                    p.focusContext = focusContext;
            }

            @Override
            public List<LayoutDependency> getDependencies(RenderableElement element) {
                return context.getDependencies(element);
            }

            @Override
            public void addLayoutDependency(RenderableElement element, LayoutDependency d) {
                context.addLayoutDependency(element, d);
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
