package ui10.geom.shape;

import ui10.geom.Insets;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;

import java.util.List;

public interface Shape {

    Shape NULL = Rectangle.of(Size.ZERO);

    List<Path> outlines();

    default Rectangle bounds() {
        return ShapeOperations.bounds(this);
    }

    default Shape translate(Point point) {
        return ShapeOperations.translate(this, point);
    }

    default Shape intoBounds(Rectangle bounds) {
        return ShapeOperations.intoBounds(this, bounds);
    }

    default Shape unionWith(Shape other) {
        return ShapeOperations.union(this, other);
    }

    default Shape intersectionWith(Shape other) {
        return ShapeOperations.intersection(this, other);
    }

    default Shape subtract(Shape other) {
        return ShapeOperations.subtract(this, other);
    }
}
