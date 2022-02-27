package ui10.geom.shape;

import ui10.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ShapeOperations {

    public static Rectangle bounds(Shape shape) {
        class ComputeBounds implements Consumer<Point> {

            private Point min, max;

            @Override
            public void accept(Point p) {
                min = Point.min(min, p);
                max = Point.max(max, p);
            }
        }

        ComputeBounds cb = new ComputeBounds();
        shape.outlines().forEach(p -> p.iterate(BézierPath.PathConsumer.visitAllPoints(cb)));
        return Rectangle.of(cb.min, cb.max);
    }

    public static Shape translate(Shape shape, Point point) {
        if (point.equals(Point.ORIGO))
            return shape;

        return new TransformedShape(shape) {
            @Override
            protected Point transform(Point p) {
                return p.add(point);
            }

            @Override
            public String toString() {
                return shape + " translated by " + point;
            }
        };
    }


    public static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

    public static Shape transform(Shape shape, UnaryOperator<Point> op) {
        return new Shape() {
            @Override
            public List<BézierPath> outlines() {
                List<BézierPath> a = shape.outlines(), b = new ArrayList<>();
                for (BézierPath p : a)
                    b.add(p.transform(op));
                return b;
            }
        };
    }

    private static abstract class TransformedShape extends Shape {
        private final Shape original;

        public TransformedShape(Shape original) {
            this.original = original;
        }

        @Override
        public List<BézierPath> outlines() {
            List<BézierPath> a = original.outlines(), b = new ArrayList<>();
            for (BézierPath p : a)
                b.add(p.transform(this::transform));
            return b;
        }

        protected abstract Point transform(Point p);
    }

    public static Shape intoBounds(Shape shape, Rectangle b) {
        if (b.isEmpty())
            // ilyenkor adjuk vissza b-t?
            throw new IllegalArgumentException(shape+", "+b);

        Rectangle a = shape.bounds();
        Shape s = new TransformedShape(shape) {
            @Override
            protected Point transform(Point p) {
                // ceilDiv(40 * 39, 41) == 39
                //assert ceilDiv((p.y() - a.top()) * (b.height() - 1), a.height() - 1) < b.height() :
                // ceilDiv((p.y() - a.top()) * (b.height() - 1), a.height() - 1) + ", " + b.height() + ", " + (p.y() - a.top()) + ", " + a;
                return new Point(
                        b.left() + ceilDiv((p.x() - a.left()) * (b.width() - 1), a.width() - 1),
                        b.top() + ceilDiv((p.y() - a.top()) * (b.height() - 1), a.height() - 1));
            }

            @Override
            public Shape intoBounds(Rectangle bounds) {
                return ShapeOperations.intoBounds(shape, bounds);
            }

            @Override
            public String toString() {
                return shape + " into " + b;
            }
        };
        assert s.bounds().equals(b.bounds()) : s.bounds() +", "+ b+", " + shape.bounds() + ", " + a.bounds();
        return s;
    }

    public static Shape union(Shape a, Shape b) {
        System.out.println("unsupported union: " + a + ", " + b + ", fallbacking to bounds union");
        return Rectangle.union(a.bounds(), b.bounds());
    }

    public static Shape intersection(Shape a, Shape b) {
        if (a.bounds().intersectionWith(b.bounds()) == null)
            return null;
        throw new UnsupportedOperationException(a+", "+b);
    }

    public static Shape subtract(Shape a, Shape b) {
        List<BézierPath> o1 = a.outlines(), o2 = b.outlines();
        if (o1.size() == 1 && o2.size() == 1)
            return new Shape() {
                @Override
                public List<BézierPath> outlines() {
                    return List.of(o2.get(0), o1.get(0));
                }
            };
        else {
            if (a.bounds().intersectionWith(b.bounds()) == null)
                return a;
            throw new UnsupportedOperationException(a+", "+b);
        }
    }
}
