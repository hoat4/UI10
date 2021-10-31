package ui10.geom.shape;

import ui10.geom.*;
import ui10.renderer6.java2d.J2DUtil;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ShapeOperations {

    public static Rectangle bounds(Shape shape) {
        class ComputeBounds extends Path.PathConsumer {

            private Point min, max;

            @Override
            protected void addPointImpl(Point p) {
                min = Point.min(min, p);
                max = Point.max(max, p);
            }
        }

        ComputeBounds cb = new ComputeBounds();
        shape.outlines().forEach(p -> {
            cb.reset();
            p.iterate(cb);
        });
        return Rectangle.of(cb.min, cb.max);
    }

    public static Shape translate(Shape shape, Point point) {
        return transform(shape, p -> p.add(point));
    }


    public static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

    public static Shape transform(Shape shape, UnaryOperator<Point> op) {
        return () -> {
            List<Path> a = shape.outlines(), b = new ArrayList<>();
            for (Path p : a)
                b.add(p.transform(op));
            return b;
        };

    }

    public static Shape intoBounds(Shape shape, Rectangle b) {
        Rectangle a = shape.bounds();
        Shape s = transform(shape, p -> new Point(
                b.left() + ceilDiv((p.x() - a.left()) * b.width(), a.width()),
                b.top() + ceilDiv((p.y() - a.top()) * b.height(), a.height())));
      //  System.out.println(s.bounds()+" vs "+b);
        return s;
    }

    public static Shape union(Shape a, Shape b) {
        throw new UnsupportedOperationException(a+", "+b);
    }

    public static Shape intersection(Shape shape, Shape other) {
        throw new UnsupportedOperationException();
    }

    public static Shape subtract(Shape a, Shape b) {
        List<Path> o1 = a.outlines(), o2 = b.outlines();
        if (o1.size() == 1 && o2.size() == 1)
            return () -> List.of(o2.get(0), o1.get(0));
        else
            throw new UnsupportedOperationException();
    }
}
