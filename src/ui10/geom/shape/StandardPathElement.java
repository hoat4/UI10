package ui10.geom.shape;

import ui10.geom.Point;

import java.util.function.UnaryOperator;

public interface StandardPathElement extends Path {

    // Point p();

    record MoveTo(Point p) implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            if (consumer.last() != null)
                throw new IllegalArgumentException();
            consumer.addPoint(p);
        }

        @Override
        public MoveTo transform(UnaryOperator<Point> op) {
            return new MoveTo(op.apply(p));
        }
    }

    record LineTo(Point p) implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            if (consumer.last() == null)
                throw new IllegalArgumentException();
            consumer.addPoint(p);
        }

        @Override
        public LineTo transform(UnaryOperator<Point> op) {
            return new LineTo(op.apply(p));
        }
    }

    record CubicCurveTo(Point p, Point control1,
                        Point control2) implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            consumer.addPoint(p); // TODO
        }

        @Override
        public CubicCurveTo transform(UnaryOperator<Point> op) {
            return new CubicCurveTo(op.apply(p), op.apply(control1), op.apply(control2));
        }
    }

    record QuadCurveTo(Point p, Point control) implements StandardPathElement {
        @Override
        public void iterate(PathConsumer consumer) {
            Point p0 = consumer.last(), p1 = control, p2 = p;

            final int tMax = 1000;
            for (int i = 0; i <= tMax; i++) {
                if (true) {
                    double t = (double) i / tMax;
                    consumer.addPoint(new Point(
                            (int) Math.round((1 - t) * (1 - t) * p0.x() + 2 * (1 - t) * t * p1.x() + t * t * p2.x()),
                            (int) Math.round((1 - t) * (1 - t) * p0.y() + 2 * (1 - t) * t * p1.y() + t * t * p2.y())
                    ));
                } else
                    consumer.addPoint(
                            p0.multiply((tMax - i) * (tMax - i)).
                                    add(control.multiply(2 * (tMax - i) * i)).
                                    add(p.multiply(i * i)).
                                    divide(tMax * tMax)
                    );
            }
        }


        @Override
        public QuadCurveTo transform(UnaryOperator<Point> op) {
            return new QuadCurveTo(op.apply(p), op.apply(control));
        }

    }

    record Close() implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            consumer.addTransformedPoint(consumer.first());
        }

        @Override
        public Path transform(UnaryOperator<Point> op) {
            return this;
        }
    }

}


