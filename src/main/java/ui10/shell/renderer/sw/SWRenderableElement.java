package ui10.shell.renderer.sw;

import ui10.base.RenderableElement;
import ui10.geom.Rectangle;

public abstract class SWRenderableElement extends RenderableElement {

    protected abstract void draw(SWRasterizer g, Rectangle rectangle);
}
