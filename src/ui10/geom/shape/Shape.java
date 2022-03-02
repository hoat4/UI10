package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.ScanLine;

import java.util.List;
import java.util.function.Consumer;

public abstract class Shape {

    public abstract List<BézierPath> outlines();

    public abstract void scan(Rectangle clip, Consumer<ScanLine> consumer);

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
        if (this instanceof Rectangle)
            return other.intersectionWith(this); // TODO
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
