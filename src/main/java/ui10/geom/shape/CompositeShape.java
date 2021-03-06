package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.HLine;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class CompositeShape extends Shape {

    List<? extends Shape> cachedSubshapes;

    public static CompositeShape of(List<? extends Shape> subshapes) {
        return new SimpleCompositeShape(subshapes);
    }

    public static class SimpleCompositeShape extends CompositeShape {

        public SimpleCompositeShape(List<? extends Shape> subshapes) {
            Objects.requireNonNull(subshapes);
            if (subshapes.isEmpty())
                throw new IllegalArgumentException("empty shape list");
            this.cachedSubshapes =subshapes;
        }

        @Override
        protected final List<? extends Shape> makeSubshapes() {
            throw new RuntimeException("should not reach here");
        }
    }

    public List<? extends Shape> shapes() {
        if (this.cachedSubshapes == null) {
            List<? extends Shape> shapes = makeSubshapes();
            Objects.requireNonNull(shapes);
            if (shapes.isEmpty())
                throw new IllegalArgumentException("empty shape list");
            cachedSubshapes = shapes;
        }
        return cachedSubshapes;
    }

    protected abstract List<? extends Shape> makeSubshapes();

    @Override
    public List<BézierPath> outlines() {
        return shapes().stream().flatMap(shape -> shape.outlines().stream()).toList();
    }

    @Override
    public void scan(Rectangle clip, Consumer<HLine> callback) {
        shapes().forEach(shape -> shape.scan(clip, callback));
    }

    @Override
    public Rectangle bounds() {
        return shapes().stream().map(Shape::bounds).reduce(Rectangle::union).orElseThrow();
    }

    private CompositeShape op(UnaryOperator<Shape> op) {
        List<Shape> shapes = this.shapes().stream().map(op).filter(Objects::nonNull).toList();
        if (shapes().isEmpty())
            return null;
        return new SimpleCompositeShape(shapes);
    }

    @Override
    public Shape translate(Point point) {
        return op(shape -> shape.translate(point));
    }

    @Override
    public Shape intoBounds(Rectangle bounds) {
        Rectangle allBounds = bounds();
        return op(shape -> {
            Rectangle shapeBounds = shape.bounds();

            return shape.intoBounds(new Rectangle(
                    (shapeBounds.left() - allBounds.left()) * bounds.width() / allBounds.width(),
                    (shapeBounds.top() - allBounds.top()) * bounds.height() / allBounds.height(),
                    (shapeBounds.width() * bounds.width() + allBounds.width() - 1) / allBounds.width(),
                    (shapeBounds.height() * bounds.height() + allBounds.height() - 1) / allBounds.height()
            ));
        });
    }

    @Override
    public Shape unionWith(Shape other) {
        return new SimpleCompositeShape(Stream.concat(
                shapes().stream(),
                other instanceof CompositeShape c ? c.shapes().stream() : Stream.of(other)
        ).toList());
    }

    @Override
    public Shape intersectionWith(Shape other) {
        if (other instanceof Rectangle r && r.contains(bounds()))
            return this;

        return op(shape -> {
            Shape s = shape.intersectionWith(other);
            if (s != null && s.bounds().isEmpty())
                throw new RuntimeException(shape+"->"+s);
            return s;
        });
    }

    @Override
    public Shape subtract(Shape other) {
        return op(shape -> shape.subtract(other));
    }

    // equals?


    @Override
    public String toString() {
        return shapes().toString();
    }
}
