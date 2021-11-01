package ui10.geom;

import java.util.function.UnaryOperator;

public record Size(int width, int height) {

    public static final Size ZERO = new Size(0, 0);

    public Size {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException(width + " × " + height);
    }

    public static Size max(Size a, Size b) {
        return new Size(Math.max(a.width, b.width), Math.max(a.height, b.height));
    }

    public static Size of(Point end) {
        return new Size(end.x(), end.y());
    }

    public Size add(Size s) {
        return new Size(width + s.width(), height + s.height());
    }

    public Size add(Point s) {
        return new Size(width + s.x(), height + s.y());
    }

    public Size subtract(Point point) {
        return new Size(width - point.x(), height - point.y());
    }

    public Size subtract(Size s) {
        try {
            return new Size(width - s.width, height - s.height);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("couldn't subtract " + s + " from " + this);
        }
    }

    public Size subtractOrClamp(Point p) {
        return new Size(Math.max(0, width - p.x()), Math.max(0, height - p.y()));
    }

    public Size subtractOrClamp(Size s) {
        return new Size(Math.max(0, width - s.width()), Math.max(0, height - s.height()));
    }

    public Size divide(int divisor) {
        return lanewise(n -> n / divisor); // itt lehet hogy inkább kerekíteni kéne, de nem biztos
    }

    private Size lanewise(UnaryOperator<Integer> op) {
        return new Size(op.apply(width), op.apply(height));
    }

    public Point asPoint() {
        return new Point(width, height);
    }

    public Point leftBottom() {
        return new Point(0, height);
    }

    public Size multiply(int i) {
        return new Size(width * i, height * i);
    }

    public boolean isZero() {
        return width == 0 || height == 0;
    }

    @Override
    public String toString() {
        return "(" + width + " × " + height + ")";
    }
}
