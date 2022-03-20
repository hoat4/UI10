package ui10.base;

import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.util.*;
import java.util.function.Consumer;

// if there are children, override enumerateStaticChildren and onShapeApplied in the subclass
public non-sealed abstract class RenderableElement extends Element {

    public RendererData rendererData;

    // ez most két helyről is be van állítva (initLogicalParent és Pane::onShapeApplied), utóbbit ki kéne szedni
    public RenderableElement parent;
    public UIContext uiContext;

    protected Shape shape;
    protected List<LayoutContext1.LayoutDependency<?, ?>> layoutDependencies;

    Map<Property<?>, Object> transientAncestorsProperties = Map.of();
    Set<Property<?>> transientDescendantInterestedProperties = new HashSet<>();

    @Override
    void initLogicalParent(Element logicalParent) {
        Map<Property<?>, Object> map = new HashMap<>();
        Element e = logicalParent;
        while (e instanceof TransientElement t) {
            map.putAll(t.props);
            e = t.logicalParent;
        }
        transientAncestorsProperties = map;
        parent = (RenderableElement) e;
    }

    @SuppressWarnings("unchecked")
    @Override
    <T> T getPropertyFromParent(Property<T> prop) {
        if (transientAncestorsProperties != null && transientAncestorsProperties.containsKey(prop))
            return (T) transientAncestorsProperties.get(prop);

        if (parent == null)
            return prop.defaultValue;
        return parent.getProperty(prop);
    }

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        // most subclasses have no children
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        boolean changed = !Objects.equals(this.shape, shape);
        this.shape = shape;
        this.layoutDependencies = context.getDependencies(this);
        if (changed && rendererData != null)
            rendererData.invalidateRendererData();

        onShapeApplied(shape);
    }

    public void dispatchElementEvent(ElementEvent event) {
        if (initialized || !(event instanceof ChangeEvent<?>))
            dispatchPropertyChangeImpl(event);
    }

    void dispatchPropertyChangeImpl(ElementEvent changeEvent) {
        onPropertyChange(changeEvent);
    }

    protected void onPropertyChange(ElementEvent changeEvent) {
    }

    protected void onShapeApplied(Shape shape) {
    }

    public Shape getShapeOrFail() {
        if (shape == null)
            throw new IllegalStateException("no shape for " + this);
        return shape;
    }

    public Point origin() {
        return getShapeOrFail().bounds().topLeft();
    }

    public void invalidate() {
        if (rendererData != null)
            rendererData.invalidateRendererData();
        if (uiContext == null)
            return;
        uiContext.requestLayout(new UIContext.LayoutTask(this, this::revalidate));
    }

    private void revalidate() {
        //System.out.println("revalidate " + this + ": " + shape);

        if (shape == null)
            throw new IllegalStateException(); // should not happen

        LayoutContext1 ctx = new LayoutContext1();

        for (LayoutContext1.LayoutDependency<?, ?> dep : layoutDependencies) {
            if (ctx.isInvalidated(this, dep)) {
                Objects.requireNonNull(parent, this::toString);
                parent.invalidate();
                return;
            }
        }

        try {
            onShapeApplied(shape);
        } catch (RuntimeException e) {
            System.err.print("Failed to layout " + this + ": ");
            e.printStackTrace();
        }
    }

    public static RenderableElement of(Element node) {
        return node instanceof RenderableElement r ? r : Pane.of(node);
    }

    @Override
    public <T> void setProperty(Property<T> prop, T value) {
        super.setProperty(prop, value);
        dispatchElementEvent(new ChangeEvent(prop, value));
    }
}
