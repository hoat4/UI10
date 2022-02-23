package ui10.base;

import ui10.geom.shape.Shape;

import java.util.*;
import java.util.function.Consumer;

public abstract class LayoutContext2 extends LayoutContext1 implements Consumer<RenderableElement> {

    private final Map<RenderableElement, List<LayoutDependency>> dependencies = new HashMap<>();

    public void placeElement(Element element, Shape shape) {
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(shape, "shape");

        if (element.replacement != null && !inReplacement.contains(element)) {
            inReplacement.add(element);
            try {
                placeElement(element.replacement, shape);
            } finally {
                inReplacement.remove(element);
            }
            return;
        }

        if (element instanceof RenderableElement r)
            accept(r);

        element.performLayoutImpl(shape, this);
    }

    /**
     * Finds which elements depend on the specified element
     */
    public List<LayoutDependency> getDependencies(RenderableElement element) {
        return dependencies.getOrDefault(element, Collections.emptyList());
    }

    @Override
    void addLayoutDependency(RenderableElement element, LayoutDependency d) {
        dependencies.computeIfAbsent(element, __ -> new ArrayList<>()).add(d);
    }

    public static LayoutContext2 ignoring() {
        return new LayoutContext2() {
            @Override
            public void accept(RenderableElement renderableElement) {
            }
        };
    }
}
