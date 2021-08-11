package ui10.renderer.java2d;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.image.Color;
import ui10.image.Fill;
import ui10.image.LinearGradient;
import ui10.image.RGBColor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class J2DUtil {

    static float i2px(int i) {
        return i / 1000f;
    }

    static int px2i(double i) {
        return (int) (i * 1000.0 + .5);
    }

    public static Rectangle rect(Rectangle2D r) {
        return new Rectangle(
                new Point(px2i(r.getMinX()), px2i(r.getMinY())),
                new Point(px2i(r.getMaxX()), px2i(r.getMinY())));
    }

    public static Rectangle2D rect(Rectangle r) {
        return new Rectangle2D.Double(
                i2px(r.topLeft().x()),
                i2px(r.topLeft().y()),
                i2px(r.size().width()),
                i2px(r.size().height())
        );
    }

    public static java.awt.Color color(Color color) {
        if (!(color instanceof RGBColor r))
            throw new UnsupportedOperationException();
        return new java.awt.Color((float) r.red(), (float) r.green(), (float) r.blue(), (float) r.alpha());
    }

    public static Point2D.Double point(Point p) {
        return new Point2D.Double(i2px(p.x()), i2px(p.y()));
    }

    public static Paint asPaint(Fill fill) {
        Objects.requireNonNull(fill);

        if (fill instanceof Color c)
            return color(c);
        else if (fill instanceof LinearGradient g) {
            float[] fractions = new float[g.stops().size()];
            java.awt.Color[] colors = new java.awt.Color[g.stops().size()];
            for (int i = 0; i < g.stops().size(); i++) {
                LinearGradient.Stop stop = g.stops().get(i);
                fractions[i] = (float) stop.fraction();
                colors[i] = color(stop.color());
            }
            return new LinearGradientPaint(point(g.start()), point(g.end()), fractions, colors);
        } else
            throw new UnsupportedOperationException(fill.toString());
    }
}
