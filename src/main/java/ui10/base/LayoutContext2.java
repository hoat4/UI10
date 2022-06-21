package ui10.base;

import ui10.geom.shape.Shape;

import java.util.*;
import java.util.function.Consumer;

public abstract class LayoutContext2 extends LayoutContext1 implements Consumer<RenderableElement> {

    // ennek nem egy hashmapnek kéne lennie, mert annak nagy az overheadje
    protected final Map<RenderableElement, List<LayoutDependency<?, ?>>> dependencies = new HashMap<>();

    public LayoutContext2(Element defaultParent) {
        super(defaultParent);
    }

    public void placeElement(Element element, Shape shape) {
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(shape, "shape");

        if (element.parent() == null)
            element.initParent(defaultParent);

        if (element instanceof RenderableElement r)
            accept(r);

        element.applyShape(shape, this);
    }

    @Override
    void addLayoutDependency(RenderableElement element, LayoutDependency<?, ?> d) {
        dependencies.computeIfAbsent(element, __ -> new ArrayList<>()).add(d);
    }

    public static LayoutContext2 ignoring(Element defaultParent) {
        return new LayoutContext2(defaultParent) {
            @Override
            public void accept(RenderableElement renderableElement) {
            }
        };
    }
}
