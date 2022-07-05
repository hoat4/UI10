package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.HLine;

import java.util.List;
import java.util.function.Consumer;

public abstract class Shape {

    public abstract List<BézierPath> outlines();

    public abstract void scan(Rectangle clip, Consumer<HLine> consumer);

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

    public boolean contains(Point p) {
        return ShapeOperations.contains(this, p);
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
