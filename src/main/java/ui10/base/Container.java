package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Container extends RenderableElement {

    private final List<RenderableElement> children = new ArrayList<>();
    private boolean valid;

    public FocusContext focusContext;

    protected void validate() {
    }

    @Override
    void dispatchPropertyChangeImpl(ElementEvent changeEvent) {
        if (subscriptions().contains(changeEvent.property()))
            onPropertyChange(changeEvent);
        for (ExternalListener<?> el : externalListeners)
            elHelper(el, changeEvent);

        List<RenderableElement> prevChildren = List.copyOf(children);

        for (RenderableElement e : children)
            if (prevChildren.contains(e) && !e.hasPropertyInSelf(changeEvent.property())
                    && !e.hasPropertyInTransientAncestor(changeEvent.property()))
                // subscriptionst is figyelembe kéne venni, de az összes descendantét valahogy
                e.dispatchPropertyChangeImpl(changeEvent);
    }


    protected void onPropertyChange(ElementEvent changeEvent) {
    }

    protected abstract Element content();

    private Element cachedContent;

    private Element getContent() {
        if (!valid) {
            validate();
            valid = true;

            cachedContent = Objects.requireNonNull(content(), () -> "null content in " + this);
            cachedContent.initParent(this);
        }

        return cachedContent;
    }

    public List<RenderableElement> renderableElements() {
        if (!valid)
            onShapeApplied(shape);
        return children;
    }

    @Override
    public void invalidate() {
        valid = false;
        super.invalidate();
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(getContent(), constraints);
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        children.clear();

        // inter-container layout dependencies are not supported currently
        new LayoutContext2() {

            @Override
            public void accept(RenderableElement e) {
                if (e.parentRenderable() == null)
                    // this should not occur, but currently does because decoration
                    e.parent = Container.this;
                else if (e.parentRenderable() != Container.this)
                    throw new IllegalStateException("not a child of " + Container.this + ": " + e);
                children.add(e);
                if (e instanceof Container p)
                    p.focusContext = focusContext;
            }
        }.placeElement(getContent(), shape);
    }

    public static Container of(Element node) {
        if (node instanceof Container p)
            return p;
        else
            return new Container() {
                @Override
                public Element content() {
                    return node;
                }

                @Override
                public String toString() {
                    return "Container[" + node + "]";
                }
            };
    }
}
