package ui10.base;

import ui10.decoration.css.DecorBox;
import ui10.geom.shape.Shape;

import java.util.*;
import java.util.function.Consumer;

public abstract class LayoutContext2 extends LayoutContext1 implements Consumer<RenderableElement> {

    // ennek nem egy hashmapnek kéne lennie, mert annak nagy az overheadje
    private final Map<RenderableElement, List<LayoutDependency<?, ?>>> dependencies = new HashMap<>();

    public void placeElement(Element element, Shape shape) {
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(shape, "shape");

        Element replacement = element.replacement;
        if (replacement != null) {
            element.replacement = null;

            try {
                if (replacement instanceof DecorBox) // fast-path
                    replacement.performLayoutImpl(shape, this);
                else
                    placeElement(replacement, shape);
            } finally {
                element.replacement = replacement;
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
    public List<LayoutDependency<?, ?>> getDependencies(RenderableElement element) {
        return dependencies.getOrDefault(element, Collections.emptyList());
    }

    @Override
    void addLayoutDependency(RenderableElement element, LayoutDependency<?, ?> d) {
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
