package ui10.shell.renderer.sw;

import ui10.base.LayoutContext1;
import ui10.base.RenderableElement;
import ui10.controls.TextElement;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public class SWTextElement extends SWRenderableElement {

    private final TextElement textElement;

    public SWTextElement(TextElement textElement) {
        this.textElement = textElement;
    }

    @Override
    protected void invalidateRendererData() {
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return constraints.clamp(new Size(50, 20));
    }

    @Override
    protected void draw(SWRasterizer g, Rectangle rectangle) {
    }
}
