package ui10.geom;

import static ui10.geom.Num.num;

public record Point(Num x, Num y) {

    public Point(int x, int y) {
        this(num(x), num(y));
    }

    public static final Point ORIGO = new Point(0, 0);

    public static Point min(Point a, Point b) {
        return new Point(Num.min(a.x, b.x), Num.min(a.y, b.y));
    }

    public static Point max(Point a, Point b) {
        return new Point(Num.max(a.x, b.x), Num.max(a.y, b.y));
    }

    public static Point of(Size size) {
        return new Point(size.width(), size.height());
    }

    public Point add(Size s) {
        return new Point(x.add(s.width()), y.add(s.height()));
    }

    public Point add(Point s) {
        return new Point(x.add(s.x()), y.add(s.y()));
    }

    public Point subtract(Size s) {
        return new Point(x.sub(s.width()), y.sub(s.height()));
    }

    public Point divide(Num num) {
        return new Point(x.div(num), y.div(num));
    }
}
