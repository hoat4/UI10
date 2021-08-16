package ui10.geom;

import static ui10.geom.Point.ORIGO;

public record Rectangle(Point topLeft, Size size) {

    public Rectangle(int x, int y, int w, int h) {
        this(new Point(x, y), new Size(w, h));
    }

    public boolean isEmpty() {
        return size.width() == 0 || size.height() == 0;
    }

    public Point rightBottom() {
        if (isEmpty())
            throw new UnsupportedOperationException();
        return topLeft.add(size).subtract(new Size(1, 1));
    }

    public Point rightTop() {
        if (isEmpty())
            throw new UnsupportedOperationException();
        return topLeft.add(size.width(), 0).subtract(new Size(1, 1));
    }

    public Point leftBottom() {
        if (isEmpty())
            throw new UnsupportedOperationException();
        return topLeft.add(0, size.height()).subtract(new Size(1, 1));
    }

    public static Rectangle union(Rectangle a, Rectangle b) {
        if (a == null || a.isEmpty())
            return b;
        if (b == null || b.isEmpty())
            return a;

        Point tl = Point.min(a.topLeft, b.topLeft);
        return new Rectangle(tl, new Size(
                Math.max(a.topLeft.x() + a.size.width(), b.topLeft.x() + b.size.width()) - tl.x(),
                Math.max(a.topLeft.y() + a.size.height(), b.topLeft.y() + b.size.height()) - tl.y()
        ));
    }

    // nullable a result
    public static Rectangle intersection(Rectangle a, Rectangle b) {
        if (a == null || a.isEmpty() || b == null || b.isEmpty())
            return null;

        return of(
                Point.max(a.topLeft, b.topLeft),
                Point.min(a.rightBottom(), b.rightBottom())
        );
    }

    public static Rectangle of(Size size) {
        return new Rectangle(ORIGO, size);
    }

    public static Rectangle of(Point topLeft, Point rightBottom) {
        return new Rectangle(topLeft,
                new Size(rightBottom.x() - topLeft.x() + 1, rightBottom.y() - topLeft.y() + 1));
    }

    public Rectangle centered(Size size) {
        // (size() - size) / 2
        return new Rectangle(topLeft.add(size().asPoint().subtract(size).divide(2)), size);
    }

    public Rectangle withSize(Size size) {
        return new Rectangle(topLeft, size);
    }

    public Rectangle withInsets(int top, int right, int bottom, int left) {
        return new Rectangle(topLeft.add(new Size(left, top)), size.subtract(new Size(right, bottom)));
    }

    public Rectangle atOrigo() {
        return new Rectangle(ORIGO, size);
    }

    public Rectangle withInsets(Insets i) {
        return withInsets(i.top(), i.right(), i.bottom(), i.left());
    }

    public int area() {
        return size().width()*size.height();
    }

//    @Override
    public boolean contains(Point point) {
        return point.x() >= topLeft.x() && point.y() >= topLeft.y() &&
                point.x() < topLeft.x() + size.width() && point.y() < topLeft.y() + size.height();
    }

//    @Override
    public int containment(Rectangle rectangle) {
        return intersection(this, rectangle).area();
    }

/*    @Override
    public Rectangle bounds() {
        return this;
    }

    @Override
    public Path toPath() {
        return new Path(topLeft, List.of(
                new Path.LineTo(rightTop()),
                new Path.LineTo(rightBottom()),
                new Path.LineTo(leftBottom()),
                new Path.LineTo(topLeft())
        ));
    }*/
}
