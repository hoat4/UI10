package ui10.ui6.layout;

import ui10.ui6.RenderableElement;

import java.util.*;
import java.util.function.Consumer;

public interface LayoutContext2 extends LayoutContext1, Consumer<RenderableElement> {

    /**
     * Finds which elements depend on the specified element
     */
    List<LayoutDependency> getDependencies(RenderableElement element);

    static LayoutContext2 ignoring(RenderableElement lowestRenderableElement) {
        return new LayoutContext2() {

            @Override
            public RenderableElement lowestRenderableElement() {
                return lowestRenderableElement;
            }

            @Override
            public List<LayoutDependency> getDependencies(RenderableElement element) {
                return Collections.emptyList();
            }

            @Override
            public void accept(RenderableElement element) {
            }

            @Override
            public void addLayoutDependency(RenderableElement element, LayoutDependency d) {
            }
        };
    }

    abstract class AbstractLayoutContext2 implements LayoutContext2 {
        private final Map<RenderableElement, List<LayoutDependency>> dependencies = new HashMap<>();

        private final RenderableElement lowestRenderableElement;

        public AbstractLayoutContext2(RenderableElement lowestRenderableElement) {
            this.lowestRenderableElement = lowestRenderableElement;
        }

        @Override
        public RenderableElement lowestRenderableElement() {
            return lowestRenderableElement;
        }

        @Override
        public List<LayoutDependency> getDependencies(RenderableElement element) {
            return dependencies.getOrDefault(element, Collections.emptyList());
        }

        @Override
        public void addLayoutDependency(RenderableElement element, LayoutDependency d) {
            dependencies.computeIfAbsent(element, __ -> new ArrayList<>()).add(d);
        }
    }
}
