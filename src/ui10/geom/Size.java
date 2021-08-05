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

    public static Size of(Point end) {
        return new Size(end.x(), end.y());
    }

    public Size add(Size s) {
        return new Size(width.add(s.width()), height.add(s.height()));
    }

    public Size add(Point s) {
        return new Size(width.add(s.x()), height.add(s.y()));
    }

    public Size subtract(Point point) {
        return new Size(width.sub(point.x()), height.sub(point.y()));
    }

    public Size subtract(Size s) {
        try {
            return new Size(width.sub(s.width()), height.sub(s.height()));
        }catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("couldn't subtract "+s +" from "+this);
        }
    }

    public Size subtractOrClamp(Point p) {
        return new Size(Num.max(Num.ZERO, width.sub(p.x())), Num.max(Num.ZERO, height.sub(p.y())));
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
