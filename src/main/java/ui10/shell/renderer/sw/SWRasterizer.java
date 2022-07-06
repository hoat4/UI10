package ui10.shell.renderer.sw;

import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import ui10.base.*;
import ui10.geom.Fraction;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.graphics.ColorFill;
import ui10.graphics.LinearGradient;
import ui10.graphics.Opacity;
import ui10.image.Color;

public class SWRasterizer {

    private Element root;
    public MemorySegment buffer;
    public Rectangle renderRect;
    private Fraction opacity;

    public void initRoot(Element element) {
        this.root = element;
    }

    public void render(Rectangle rectangle, MemorySegment buffer) {
        this.buffer = buffer;
        renderRect = rectangle;
        opacity = Fraction.WHOLE;
        renderElement(root);
    }

    private void renderElement(Element e) {
        if (e.shape().bounds().intersectionWith(renderRect) == null)
            return;

        // MemoryAccess.setIntAtIndex(buffer, coord(Point.ORIGO), 0xFF880000);

        e = e.renderableElement();

        switch (e) {
            case LayoutElement p -> {
                LayoutElement.performLayoutHelper(p, p.shape(), new LayoutContext2(p) {
                    @Override
                    public void accept(Element e) {
                        renderElement(e);
                    }
                });
            }
            case ColorFill colorFill -> {
                //Shape shape = colorFill.getShapeOrFail().intersectionWith(rect);
                //if (shape != null)
                fill(colorFill.color(), colorFill.shape());
            }
            case LinearGradient linearGradient -> {
                fill(linearGradient.stops.get(0).color(), linearGradient.shape());
            }
            case Opacity opacityElement -> {
                Fraction prevOpacity = this.opacity;
                this.opacity = opacityElement.fraction;
                renderElement(opacityElement.content);
                this.opacity = prevOpacity;
            }
            case SWRenderableElement element -> {
                element.draw(this, element.shape().bounds());
            }
            default -> {
                System.err.println("Unknown element: " + e);
            }
        }
    }

    private void fill(Color color, Shape shape) {
        System.out.println("fill "+shape+" with "+color);
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
