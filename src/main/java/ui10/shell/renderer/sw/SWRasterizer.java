/*
package ui10.shell.renderer.sw;

import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import ui10.base.Container;
import ui10.base.RenderableElement;
import ui10.geom.Fraction;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.graphics.ColorFill;
import ui10.graphics.LinearGradient;
import ui10.graphics.Opacity;
import ui10.image.Color;

public class SWRasterizer {

    private RenderableElement root;
    public MemorySegment buffer;
    public Rectangle renderRect;
    private Fraction opacity;

    public void initRoot(RenderableElement element) {
        this.root = element;
    }

    public void render(Rectangle rectangle, MemorySegment buffer) {
        this.buffer = buffer;
        renderRect = rectangle;
        opacity = Fraction.WHOLE;
        renderElement(root);
    }

    private void renderElement(RenderableElement e) {
        if (e.getShapeOrFail().bounds().intersectionWith(renderRect) == null)
            return;

        // MemoryAccess.setIntAtIndex(buffer, coord(Point.ORIGO), 0xFF880000);

        switch (e) {
            case Container p -> {
                for (RenderableElement element : p.renderableElements())
                    renderElement(element);
            }
            case ColorFill colorFill -> {
                //Shape shape = colorFill.getShapeOrFail().intersectionWith(rect);
                //if (shape != null)
                fill(colorFill.color(), colorFill.getShapeOrFail());
            }
            case LinearGradient linearGradient -> {
                fill(linearGradient.stops.get(0).color(), linearGradient.getShapeOrFail());
            }
            case Opacity opacityElement -> {
                Fraction prevOpacity = this.opacity;
                this.opacity = opacityElement.fraction;
                renderElement(opacityElement.content);
                this.opacity = prevOpacity;
            }
            case SWRenderableElement element ->{
                element.draw(this, element.getShapeOrFail().bounds());
            }
            default -> {
                System.err.println("Unknown element: " + e);
            }
        }
    }

    private void fill(Color color, Shape shape) {
        int argb = color.toRGBColor().toIntARGB(); // TODO opacity
        final Rectangle r = shape.bounds();
        shape.scan(r, scanline -> {
            for (int pos = coord(scanline.origin()), end = pos + scanline.width(); pos < end; pos++)
                MemoryAccess.setIntAtIndex(buffer, pos, argb);
        });
    }

    public int coord(Point p) {
        return p.y() * renderRect.width() + p.x();
    }
}
*/