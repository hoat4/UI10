package ui10.geom.shape;

import org.w3c.dom.css.Rect;
import ui10.geom.Fraction;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.ScanLine;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class CompositeShape extends Shape {

    public final List<Shape> shapes;

    public CompositeShape(List<Shape> shapes) {
        if (shapes.isEmpty())
            throw new IllegalArgumentException("empty shape list");
        this.shapes = shapes;
    }

    @Override
    public List<BézierPath> outlines() {
        return shapes.stream().flatMap(shape -> shape.outlines().stream()).toList();
    }

    @Override
    public void scan(Rectangle clip, Consumer<ScanLine> callback) {
        shapes.forEach(shape -> shape.scan(clip, callback));
    }

    @Override
    public Rectangle bounds() {
        return shapes.stream().map(Shape::bounds).reduce(Rectangle::union).orElseThrow();
    }

    private CompositeShape op(UnaryOperator<Shape> op) {
        List<Shape> shapes = this.shapes.stream().map(op).filter(Objects::nonNull).toList();
        if (shapes.isEmpty())
            return null;
        return new CompositeShape(shapes);
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
        return new CompositeShape(Stream.concat(
                shapes.stream(),
                other instanceof CompositeShape c ? c.shapes.stream() : Stream.of(other)
        ).toList());
    }

    @Override
    public Shape intersectionWith(Shape other) {
        return op(shape -> shape.intersectionWith(other));
    }

    @Override
    public Shape subtract(Shape other) {
        return op(shape -> shape.subtract(other));
    }

    // equals?
}
