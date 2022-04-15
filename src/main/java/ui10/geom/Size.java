package ui10.geom;

import java.util.function.UnaryOperator;

public record Size(int width, int height) {

    public static final Size ZERO = new Size(0, 0);

    // konkrét értéknek nincs gyakorlati jelentősége, de legyen kisebb mint INFINITY
    public static final int MAX = Integer.MAX_VALUE / 2 - 2;
    public static final int INFINITY = Integer.MAX_VALUE;

    public Size {
        if (width < 0 || height < 0
                || width > MAX && width != INFINITY
                || height > MAX && height != INFINITY)
            throw new IllegalArgumentException(width + " × " + height);
    }

    public static Size max(Size a, Size b) {
        return new Size(Math.max(a.width, b.width), Math.max(a.height, b.height));
    }

    public static Size of(Point end) {
        return new Size(end.x(), end.y());
    }

    public static Size of(Axis firstAxis, int a, int b) {
        switch (firstAxis) {
            case HORIZONTAL:
                return new Size(a, b);
            case VERTICAL:
                return new Size(b, a);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public int value(Axis axis) {
        switch (axis) {
            case HORIZONTAL:
                return width;
            case VERTICAL:
                return height;
            default:
                throw new UnsupportedOperationException();
        }
    }


    public Size with(Axis axis, int value) {
        return of(axis, value, value(axis.other()));
    }

    public Size withWidth(int width) {
        return new Size(width, height);
    }

    public Size withHeight(int height) {
        return new Size(width, height);
    }

    public Size add(Size s) {
        return new Size(width + s.width(), height + s.height());
    }

    public Size add(Point s) {
        return new Size(width + s.x(), height + s.y());
    }

    public Size subtract(Point point) {
        return subtract(Size.of(point));
    }

    public Size subtract(Size s) {
        try {
            return new Size(subtract(width, s.width), subtract(height, s.height));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("couldn't subtract " + s + " from " + this);
        }
    }

    public Size subtractWidth(int w) {
        try {
            return new Size(subtract(width, w), height);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("couldn't subtract width " + w + " from " + this);
        }
    }

    public Size subtractHeight(int h) {
        try {
            return new Size(width, subtract(height, h));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("couldn't subtract height " + h + " from " + this);
        }
    }

    public Size subtractOrClamp(Point p) {
        return new Size(Math.max(0, subtract(width, p.x())), Math.max(0, subtract(height, p.y())));
    }

    public Size subtractOrClamp(Size s) {
        return new Size(Math.max(0, subtract(width, s.width())), Math.max(0, subtract(height, s.height())));
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

    public static int subtract(int a, int b) {
        if (a == INFINITY)
            return Integer.MAX_VALUE;
        if (b == INFINITY)
            throw new IllegalArgumentException("can't subtract infinity from " + a);
        return a - b;
    }

    public boolean isInfinite() {
        return width == INFINITY || height == INFINITY;
    }
}
