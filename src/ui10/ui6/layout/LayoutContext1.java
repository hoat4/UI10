package ui10.ui6.layout;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.RenderableElement;

public interface LayoutContext1 {

    void addLayoutDependency(RenderableElement element, LayoutDependency d);

    record LayoutDependency(BoxConstraints inputConstraints, Shape shape) {
    }
}
