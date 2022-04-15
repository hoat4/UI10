package ui10.geom.shape;

import ui10.geom.Point;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class BézierPath {

    private final List<Integer> data;

    public BézierPath(List<Integer> data) {
        this.data = data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void iterate(PathConsumer consumer) {
        consumer.moveTo(new Point(data.get(0), data.get(1)));
        for (int i = 2; i < data.size(); ) {
            int type = data.get(i++);
            Point p = new Point(data.get(i++), data.get(i++));
            switch (type) {
                case 1 -> consumer.lineTo(p);
                case 2 -> consumer.quadCurveTo(p,
                        new Point(data.get(i++), data.get(i++)));
                case 3 -> consumer.cubicCurveTo(p,
                        new Point(data.get(i++), data.get(i++)), new Point(data.get(i++), data.get(i++)));
                default -> throw new RuntimeException("invalid order number for Bézier curve: " + type);
            }
        }
    }

    public BézierPath transform(UnaryOperator<Point> transformation) {
        Builder builder = builder();
        iterate(PathConsumer.transform(transformation, builder.asPathConsumer()));
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof BézierPath p && data.equals(p.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    public interface PathConsumer {

        void moveTo(Point p);

        void lineTo(Point p);

        void quadCurveTo(Point p, Point control);

        void cubicCurveTo(Point p, Point control1, Point control2);

        static PathConsumer visitAllPoints(Consumer<Point> consumer){
            return new PathConsumer() {
                @Override
                public void moveTo(Point p) {
                    consumer.accept(p);
                }

                @Override
                public void lineTo(Point p) {
                    consumer.accept(p);
                }

                @Override
                public void quadCurveTo(Point p, Point control) {
                    consumer.accept(p);
                    consumer.accept(control);
                }

                @Override
                public void cubicCurveTo(Point p, Point control1, Point control2) {
                    consumer.accept(p);
                    consumer.accept(control1);
                    consumer.accept(control2);
                }
            };
        }

        static PathConsumer transform(UnaryOperator<Point> transformation, PathConsumer sink){
            return new PathConsumer() {
                @Override
                public void moveTo(Point p) {
                    sink.moveTo(transformation.apply(p));
                }

                @Override
                public void lineTo(Point p) {
                    sink.lineTo(transformation.apply(p));
                }

                @Override
                public void quadCurveTo(Point p, Point control) {
                    sink.quadCurveTo(transformation.apply(p), transformation.apply(control));
                }

                @Override
                public void cubicCurveTo(Point p, Point control1, Point control2) {
                    sink.cubicCurveTo(transformation.apply(p), transformation.apply(control1), transformation.apply(control2));
                }

                @Override
                public String toString() {
                    return "Transform using "+transformation+" and feed to "+sink;
                }
            };
        }
    }

    public static class Builder {

        private List<Integer> data = new ArrayList<>();

        private Builder() {
        }

        private void put(Point p) {
            data.add(p.x());
            data.add(p.y());
        }

        public Builder moveTo(Point p) {
            if (data.isEmpty())
                put(p);
            else
                throw new IllegalStateException("path already started");

            return this;
        }

        public Builder lineTo(Point p) {
            data.add(1);
            put(p);
            return this;
        }

        public Builder quadCurveTo(Point p, Point control) {
            data.add(2);
            put(p);
            put(control);
            return this;
        }

        public Builder cubicCurveTo(Point p, Point control1, Point control2) {
            data.add(3);
            put(p);
            put(control1);
            put(control2);
            return this;
        }

        public Builder close() {
            data.add(1);
            data.add(data.get(0));
            data.add(data.get(1));
            return this;
        }

        public PathConsumer asPathConsumer() {
            return new PathConsumer() {
                @Override
                public void moveTo(Point p) {
                    Builder.this.moveTo(p);
                }

                @Override
                public void lineTo(Point p) {
                    Builder.this.lineTo(p);
                }

                @Override
                public void quadCurveTo(Point p, Point control) {
                    Builder.this.quadCurveTo(p, control);
                }

                @Override
                public void cubicCurveTo(Point p, Point control1, Point control2) {
                    Builder.this.cubicCurveTo(p, control1, control2);
                }
            };
        }

        public BézierPath build() {
            BézierPath p = new BézierPath(data);
            data = null;
            return p;
        }

    }
}

/*
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
 */