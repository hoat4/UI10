package ui10.layout;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;

import java.util.Objects;

public record BoxConstraints(Size min, int maxWidth, int maxHeight) {

    public static final int INFINITY = Integer.MAX_VALUE;

    public BoxConstraints {
        if (maxWidth < min.width() || maxHeight < min.height())
            throw new IllegalArgumentException(min + ", " + maxWidth + "×" + maxHeight);
    }

    public BoxConstraints(Size min, Size max) {
        this(min, max.width(), max.height());
    }

    public static BoxConstraints fixed(Size size) {
        return new BoxConstraints(size, size);
    }

    public BoxConstraints subtract(Point point) {
        Objects.requireNonNull(point);
        if (point.x() > maxWidth || point.y() > maxHeight)
            throw new IllegalArgumentException("couldn't subtract " + point + " from " + this);
        return new BoxConstraints(
                min.subtractOrClamp(point),
                subtract(maxWidth, point.x()), subtract(maxHeight, point.y())
        );
    }

    public BoxConstraints subtract(Size size) {
        Objects.requireNonNull(size);
        if (size.width() > maxWidth || size.height() > maxHeight)
            throw new IllegalArgumentException("couldn't subtract " + size + " from " + this);
        return new BoxConstraints(
                min.subtractOrClamp(size),
                subtract(maxWidth, size.width()), subtract(maxHeight, size.height())
        );
    }

    public BoxConstraints withMinimum(Size min) {
        return new BoxConstraints(min, maxWidth, maxHeight);
    }

    public Size clamp(Size size) {
        return new Size(
                Math.max(min.width(), Math.min(maxWidth, size.width())),
                Math.max(min.height(), Math.min(maxHeight, size.height()))
        );
    }

    public Size maxSizeOrFail() {
        return new Size(maxWidth, maxHeight); // ez a konstruktor exceptiont dob, ha túl nagy valamelyik érték
    }

    public BoxConstraints withUnboundedWidth() {
        return new BoxConstraints(min, INFINITY, maxHeight);
    }

    public BoxConstraints withUnboundedHeight() {
        return new BoxConstraints(min, maxWidth, INFINITY);
    }

    public Shape clamp(Shape shape) {
        shape = shape.unionWith(Rectangle.of(min));

        Size maxSize;
        if (maxWidth == INFINITY)
            if (maxHeight == INFINITY)
                return shape;
            else
                maxSize = new Size(shape.bounds().width(), maxHeight);
        else if (maxHeight == INFINITY)
            maxSize = new Size(maxWidth, shape.bounds().height());
        else
            maxSize = maxSizeOrFail();

        return shape.intersectionWith(Rectangle.of(maxSize));
    }

    public boolean contains(Size size) {
        return true; // TODO
    }

    public BoxConstraints withWidth(int min, int max) {
        return new BoxConstraints(new Size(min, this.min.height()), max, maxHeight);
    }

    public BoxConstraints withHeight(int min, int max) {
        return new BoxConstraints(new Size(this.min.width(), min), maxWidth, max);
    }

    public boolean containsWidth(int w) {
        return w >= min.width() && w <= maxWidth;
    }

    public boolean isFixed() {
        return min.width() == maxWidth && min.height() == maxHeight;
    }

    public static int subtract(int a, int b) {
        if (a == INFINITY)
            return Integer.MAX_VALUE;
        if (b == INFINITY)
            throw new IllegalArgumentException("can't subtract infinity from " + a);
        return a - b;
    }
}
