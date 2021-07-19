package ui10.renderer.java2d;

import ui10.geom.FloatingPointNumber;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.image.Color;
import ui10.image.RGBColor;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class J2DUtil {
    public static Rectangle rect(Rectangle2D r) {
        return new Rectangle(
                new Point(new FloatingPointNumber(r.getMinX()), new FloatingPointNumber(r.getMinY())),
                new Point(new FloatingPointNumber(r.getMaxX()), new FloatingPointNumber(r.getMinY())));
    }

    public static Rectangle2D rect(Rectangle r) {
        return new Rectangle2D.Double(
                r.topLeft().x().toDouble(),
                r.topLeft().y().toDouble(),
                r.size().width().toDouble(),
                r.size().height().toDouble()
        );
    }

    public static Paint color(Color color) {
        if (!(color instanceof RGBColor r))
            throw new UnsupportedOperationException();
        return new java.awt.Color((float) r.red(), (float) r.green(), (float) r.blue(), (float) r.alpha());
    }
}
