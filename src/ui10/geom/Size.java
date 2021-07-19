package ui10.geom;

import java.util.function.UnaryOperator;

import static ui10.geom.Num.num;

public record Size(Num width, Num height) {
    public static final Size ZERO = new Size(0, 0);

    public Size {
        if (width.isNegative() || height.isNegative())
            throw new IllegalArgumentException(width+" × "+height);
    }

    public Size(int width, int height) {
        this(num(width), num(height));
    }

    public static Size max(Size a, Size b) {
        return new Size(Num.max(a.width, b.width),
                Num.max(a.height, b.height));
    }

    public Size add(Size s) {
        return new Size(width.add(s.width()), height.add(s.height()));
    }

    public Size subtract(Point point) {
        return new Size(width.sub(point.x()), height.sub(point.y()));
    }

    public Size subtract(Size s) {
        return new Size(width.sub(s.width()), height.sub(s.height()));
    }

    public Size subtractOrClamp(Size s) {
        return new Size(Num.max(Num.ZERO, width.sub(s.width())), Num.max(Num.ZERO, height.sub(s.height())));
    }

    public Size divide(Num divisor) {
        return lanewise(n -> n.div(divisor));
    }

    private Size lanewise(UnaryOperator<Num> op) {
        return new Size(op.apply(width), op.apply(height));
    }

    public Point asPoint() {
        return new Point(width, height);
    }
}
