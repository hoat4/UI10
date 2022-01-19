package ui10.layout;


import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;

import java.util.Objects;

public record BoxConstraints(Size min, Size max) {

    public BoxConstraints {
        if (max.width() < min.width() || max.height() < min.height())
            throw new IllegalArgumentException(min + ", " + max);
    }

    public static BoxConstraints fixed(Size size) {
        return new BoxConstraints(size, size);
    }

    public BoxConstraints subtract(Point point) {
        Objects.requireNonNull(point);
        if (point.x() > max.width() || point.y() > max.height())
            throw new IllegalArgumentException("couldn't subtract " + point + " from " + this);
        return new BoxConstraints(min.subtractOrClamp(point), max.subtract(point));
    }

    public BoxConstraints subtract(Size size) {
        Objects.requireNonNull(size);
        return new BoxConstraints(min.subtractOrClamp(size), max.subtract(size));
    }

    public BoxConstraints withMinimum(Size min) {
        return new BoxConstraints(min, max);
    }

    public Size clamp(Size size) {
        return new Size(
                Math.max(min.width(), Math.min(max.width(), size.width())),
                Math.max(min.height(), Math.min(max.height(), size.height()))
        );
    }

    public Shape clamp(Shape shape) {
        return shape.unionWith(Rectangle.of(min)).intersectionWith(Rectangle.of(max));
    }

    public boolean contains(Size size) {
        return true; // TODO
    }

    public BoxConstraints withWidth(int min, int max) {
        return new BoxConstraints(new Size(min, this.min.height()), new Size(max, this.max.height()));
    }

    public BoxConstraints withHeight(int min, int max) {
        return new BoxConstraints(new Size(this.min.width(), min), new Size(this.max.width(), max));
    }

    public boolean containsWidth(int w) {
        return w >= min.width() && w <= max.width();
    }

    public boolean isFixed() {
        return min.equals(max);
    }
}
