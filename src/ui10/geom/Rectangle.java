package ui10.geom;

import static ui10.geom.NumericValue.num;

public record Rectangle(Point topLeft, Point rightBottom) {

    public Rectangle(Point topLeft, Size size) {
        this(topLeft, topLeft.add(size));
    }

    public Size size() {
        return new Size(rightBottom.x().sub(topLeft.x()),
                rightBottom.y().sub(topLeft.y()), rightBottom.z().sub(topLeft.z()));
    }

    public static Rectangle rect(Point a, Point b) {
        // TODO pontok cseréje, ha szüksges
        return new Rectangle(a, b);
    }

    public static Rectangle union(Rectangle a, Rectangle b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new Rectangle(Point.min(a.topLeft, b.topLeft), Point.max(a.rightBottom, b.rightBottom));
    }

    public static Rectangle of(Size size) {
        return new Rectangle(Point.ORIGO, new Point(size.width(), size.height(), size.depth()));
    }

    public Rectangle centered(Size size) {
        // (size() - size) / 2
        return new Rectangle(topLeft.add(size().subtract(size).divide(num(2))), size);
    }

}
