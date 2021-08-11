package ui10.geom;

import java.util.Objects;

import static ui10.geom.Point.ORIGO;

public record Rectangle(Point topLeft, Point rightBottom) {

    public Rectangle(Point topLeft, Size size) {
        this(topLeft, topLeft.add(Objects.requireNonNull(size, "size")));
    }

    public Size size() {
        return new Size(rightBottom.x() - topLeft.x(), rightBottom.y() - topLeft.y());
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
        return new Rectangle(ORIGO, new Point(size.width(), size.height()));
    }

    public Rectangle centered(Size size) {
        // (size() - size) / 2
        return new Rectangle(topLeft.add(size().asPoint().subtract(size).divide(2)), size);
    }

    public Rectangle withSize(Size size) {
        return new Rectangle(topLeft, topLeft.add(size));
    }

    public Rectangle withInsets(int top, int right, int bottom, int left) {
        return new Rectangle(topLeft.add(new Size(left, top)), rightBottom.subtract(new Size(right, bottom)));
    }

    public Rectangle atOrigo() {
        return new Rectangle(ORIGO, Point.of(size()));
    }

    public Rectangle withInsets(Insets i) {
        return withInsets(i.top(), i.right(), i.bottom(), i.left());
    }
}
