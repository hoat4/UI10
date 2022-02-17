package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;

import java.util.List;

public abstract class Shape {

    public static final Shape NULL = Rectangle.of(Size.ZERO);

    public abstract List<BézierPath> outlines();

    public Rectangle bounds() {
        return ShapeOperations.bounds(this);
    }

    public Shape translate(Point point) {
        return ShapeOperations.translate(this, point);
    }

    public Shape intoBounds(Rectangle bounds) {
        return ShapeOperations.intoBounds(this, bounds);
    }

    public Shape unionWith(Shape other) {
        return ShapeOperations.union(this, other);
    }

    // nullable
    public Shape intersectionWith(Shape other) {
        return ShapeOperations.intersection(this, other);
    }

    public Shape subtract(Shape other) {
        return ShapeOperations.subtract(this, other);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Shape s && outlines().equals(s.outlines());
    }

    @Override
    public int hashCode() {
        return outlines().hashCode();
    }
}
