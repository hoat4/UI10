package ui10.shell.renderer.java2d;

import ui10.geom.*;
import ui10.geom.shape.BézierPath;
import ui10.geom.shape.Shape;
import ui10.image.Color;
import ui10.image.Fill;
import ui10.image.LinearGradient;
import ui10.image.RGBColor;

import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class J2DUtil {

    static float i2px(int i) {
        return i / 1f;
    }

    static int px2i(double i) {
        return (int) (i * 1.0 + .5);
    }

    public static Rectangle rect(Rectangle2D r) {
        return new Rectangle(
                new Point(px2i(r.getMinX()), px2i(r.getMinY())),
                new Size(px2i(r.getWidth()), px2i(r.getHeight())));
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


    public static Path2D.Double shapeToPath2D(Shape shape) {
        PathBuilder pb = new PathBuilder();
        if (shape.bounds().isEmpty())
            return pb.path;
        for (BézierPath p : shape.outlines()) {
            p.iterate(pb);
        }
        return pb.path;
    }

    private static class PathBuilder implements BézierPath.PathConsumer {

        public final Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD);

        @Override
        public void moveTo(Point p) {
            path.moveTo(p.x(), p.y());
        }

        @Override
        public void lineTo(Point p) {
            path.lineTo(p.x(), p.y());
        }

        @Override
        public void quadCurveTo(Point p, Point control) {
            path.quadTo(control.x(), control.y(), p.x(), p.y());
        }

        @Override
        public void cubicCurveTo(Point p, Point control1, Point control2) {
            path.curveTo(control1.x(), control1.y(),control2.x(), control2.y(), p.x(), p.y());
        }
    }
}
