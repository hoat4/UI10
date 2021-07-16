package ui10.geom;

import static ui10.geom.NumericValue.num;

public record Point(NumericValue x, NumericValue y, NumericValue z) {

    public Point(int x, int y, int z) {
        this(num(x), num(y), num(z));
    }

    public static final Point ORIGO = new Point(0, 0, 0);

    public static Point min(Point a, Point b) {
        return new Point(NumericValue.min(a.x, b.x), NumericValue.min(a.y, b.y), NumericValue.min(a.z, b.z));
    }

    public static Point max(Point a, Point b) {
        return new Point(NumericValue.max(a.x, b.x), NumericValue.max(a.y, b.y), NumericValue.max(a.z, b.z));
    }

    public Point add(Size s) {
        return new Point(x.add(s.width()), y.add(s.height()), z.add(s.depth()));
    }
}
