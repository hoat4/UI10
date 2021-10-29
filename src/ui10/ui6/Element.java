package ui10.ui6;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public interface Element {

    Shape computeShape(BoxConstraints constraints);

    // ez miért nem csak Pane-ben és LayoutNode-ban van? Pane-en kívüli RenderableNode-nál nincs értelme
    void applyShape(Shape shape, Consumer<Surface> consumer);
}
