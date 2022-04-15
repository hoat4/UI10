package ui10.geom.shape;

import ui10.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
            public Rectangle bounds() {
                return shape.bounds().translate(point);
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

        @Override
        public void scan(Rectangle clip, Consumer<HLine> consumer) {
            //Rectangle transformedClip = Rectangle.of(reverseTransform(clip.topLeft()), reverseTransform(clip.bottomRight()));
            original.scan(original.bounds(), scanLine -> {
                // ez így nem fog működni, ha a transform figyelembe veszi Y-t is az X kiszámításához
                consumer.accept(new HLine(transform(scanLine.origin()), transform(scanLine.rightPoint())).clip(clip));
            });
        }

        /**
         * From original shape coordinate system to this {@linkplain TransformedShape} coordinate system
         */
        protected abstract Point transform(Point p);

        // /**
        //  * From this {@linkplain TransformedShape} coordinate system to original shape coordinate system
        //  */
        // protected abstract Point reverseTransform(Point p);
    }

    public static Shape intoBounds(Shape shape, Rectangle b) {
        if (b.isEmpty())
            return b;

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
            public Rectangle bounds() {
                return b;
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

        return s;
    }

    public static Shape union(Shape a, Shape b) {
        System.out.println("unsupported union: " + a + ", " + b + ", fallbacking to bounds union");
        return Rectangle.union(a.bounds(), b.bounds());
    }

    public static Shape intersection(Shape a, Shape b) {
        Rectangle boundsIntersection = a.bounds().intersectionWith(b.bounds());
        if (boundsIntersection == null)
            return null;
        throw new UnsupportedOperationException(a + ", " + b+" (bounds intersection: "+boundsIntersection+")");
    }

    public static Shape subtract(Shape a, Shape b) {
        List<BézierPath> o1 = a.outlines(), o2 = b.outlines();
        if (o1.size() == 1 && o2.size() == 1)
            return new Shape() {

                @Override
                public List<BézierPath> outlines() {
                    return List.of(o2.get(0), o1.get(0));
                }

                @Override
                public void scan(Rectangle clip, Consumer<HLine> consumer) {
                    List<HLine> otherLines = new ArrayList<>();
                    b.scan(clip, otherLines::add);
                    if (otherLines.isEmpty()) {
                        a.scan(clip, consumer);
                        return;
                    }

                    a.scan(clip, scanLine -> {
                        List<HLine> a1 = new ArrayList<>();
                        List<HLine> b1 = new ArrayList<>();
                        a1.add(scanLine);
                        for (HLine l2 : otherLines) {
                            for (HLine l1 : a1) {
                                if (l1.y() != l2.y())
                                    continue;
                                if (l2.right() <= l1.left() || l2.left() >= l1.right())
                                    continue;

                                if (l2.left() > l1.left())
                                    b1.add(new HLine(l1.y(), l1.left(), l2.left()));
                                if (l2.right() < l1.right())
                                    b1.add(new HLine(l1.y(), l2.right(), l1.right()));
                            }

                            List<HLine> tmp = b1;
                            b1 = a1;
                            a1 = tmp;
                            b1.clear();
                        }
                        a1.forEach(consumer);
                    });
                }

                @Override
                public String toString() {
                    return "Subtract " + b + " from " + a;
                }
            };
        else {
            if (a.bounds().intersectionWith(b.bounds()) == null)
                return a;
            throw new UnsupportedOperationException(a + ", " + b);
        }
    }
}
