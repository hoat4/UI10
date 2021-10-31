package ui10.renderer6.java2d;

import ui10.geom.*;
import ui10.geom.shape.Path;
import ui10.geom.shape.Shape;
import ui10.geom.shape.StandardPathElement;
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
        PathBuilder pb = new PathBuilder(IntTransformationMatrix.IDENTITY, null);
        for (Path p : shape.outlines()) {
            pb.reset();
            p.iterate(pb);
        }
        return pb.p;
    }

    private static class PathBuilder extends Path.PathConsumer {

        public final Path2D.Double p = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        private boolean first = true;

        public PathBuilder(Transformation transformation, Shape clip) {
            super(transformation, clip);
        }

        protected void reset() {
            super.reset();
            first = true;
        }

        @Override
        protected void addPointImpl(ui10.geom.Point point) {
            if (first) {
                p.moveTo(point.x(), point.y());
                first = false;
            } else
                p.lineTo(point.x(), point.y());
        }

        @Override
        public void addSubpath(Path path) {
            if (path instanceof StandardPathElement.QuadCurveTo) {
                StandardPathElement.QuadCurveTo q = (StandardPathElement.QuadCurveTo) path;
                p.quadTo(q.control().x(), q.control().y(), q.p().x(), q.p().y());
            } else
                super.addSubpath(path);
        }
    }
}
