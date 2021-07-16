package ui10.renderer.java2d;

import ui10.geom.FloatingPointNumber;
import ui10.geom.Point;
import ui10.geom.Rectangle;

import java.awt.geom.Rectangle2D;

import static ui10.geom.NumericValue.ZERO;

public class J2DUtil {
    public static Rectangle rect(Rectangle2D r) {
        return new Rectangle(
                new Point(new FloatingPointNumber(r.getMinX()), new FloatingPointNumber(r.getMinY()), ZERO),
                new Point(new FloatingPointNumber(r.getMaxX()),new FloatingPointNumber(r.getMinY()), ZERO));
    }
}
