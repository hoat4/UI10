package ui10.ui6.layout;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.RenderableElement;

public interface LayoutContext1 {

    RenderableElement lowestRenderableElement(); // ez meg mi a fene?

    /**
     * Records that the current element must be layouted again if the size of the specified element is changed.
     */
    void addLayoutDependency(RenderableElement element, LayoutDependency d);

    record LayoutDependency(BoxConstraints inputConstraints, Shape shape) {
    }
}
