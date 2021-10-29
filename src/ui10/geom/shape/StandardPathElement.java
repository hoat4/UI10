package ui10.geom.shape;

import ui10.geom.Point;

public interface StandardPathElement extends Path {

    // Point p();

    record MoveTo(Point p) implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            if (consumer.last() != null)
                throw new IllegalArgumentException();
            consumer.addPoint(p);
        }
    }

    record LineTo(Point p) implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            if (consumer.last() == null)
                throw new IllegalArgumentException();
            consumer.addPoint(p);
        }
    }

    record CubicCurveTo(Point p, Point control1,
                        Point control2) implements StandardPathElement {
        
        @Override
        public void iterate(PathConsumer consumer) {
            consumer.addPoint(p); // TODO
        }
    }

    record QuadCurveTo(Point p, Point control) implements StandardPathElement {
        @Override
        public void iterate(PathConsumer consumer) {
            consumer.addPoint(p); // TODO
        }
    }

    record Close() implements StandardPathElement {

        @Override
        public void iterate(PathConsumer consumer) {
            consumer.addTransformedPoint(consumer.first());
        }
    }

}


