package ui10.layout;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;

import java.util.Objects;

import static ui10.geom.Size.INFINITY;

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
        return new BoxConstraints(
                min.subtractOrClamp(point),
                max.subtract(point)
        );
    }

    public BoxConstraints subtract(Size size) {
        Objects.requireNonNull(size);
        if (size.width() > max.width() || size.height() > max.height())
            throw new IllegalArgumentException("couldn't subtract " + size + " from " + this);
        return new BoxConstraints(
                min.subtractOrClamp(size),
                max.subtract(size)
        );
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

    public BoxConstraints withUnboundedWidth() {
        return new BoxConstraints(min, new Size(INFINITY, max.height()));
    }

    public BoxConstraints withUnboundedHeight() {
        return new BoxConstraints(min, new Size(max.width(), INFINITY));
    }

    public Shape clamp(Shape shape) {
        shape = shape.unionWith(Rectangle.of(min));

        return shape.intersectionWith(Rectangle.of(max));
    }

    public boolean contains(Size size) {
        return size.width() >= min.width() && size.width() <= max.width()
                && size.height() >= min.height() && size.height() <= max.height();
    }

    public BoxConstraints withWidth(int min, int max) {
        return new BoxConstraints(this.min.withWidth(min), this.max.withWidth(max));
    }

    public BoxConstraints withHeight(int min, int max) {
        return new BoxConstraints(this.min.withHeight(min), this.max.withHeight(max));
    }

    public boolean containsWidth(int w) {
        return w >= min.width() && w <= max.width();
    }

    public boolean isFixed() {
        return min.width() == max.width() && min.height() == max.height();
    }
}
