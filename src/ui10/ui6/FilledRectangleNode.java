package ui10.ui6;

import ui10.geom.Size;
import ui10.image.Fill;
import ui10.layout.BoxConstraints;

public class FilledRectangleNode extends RenderableNode {

    private Fill fill;

    public Fill fill() {
        return fill;
    }

    public FilledRectangleNode fill(Fill fill) {
        this.fill = fill;
        return this;
    }

    @Override
    public Size computeSize(BoxConstraints constraints) {
        return constraints.min();
    }

    @Override
    public void applySize(Size size, LayoutNode.LayoutContext layoutContext) {
    }
}
