package ui10.geom;

import ui10.geom.shape.Path;
import ui10.geom.shape.Polyline;
import ui10.geom.shape.Shape;

import java.util.List;

import static ui10.geom.Point.ORIGO;

public record Rectangle(Point topLeft, Size size) implements Shape {

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
        return topLeft.add(size.width(), 0).subtract(new Size(1, 0));
    }

    public Point leftBottom() {
        if (isEmpty())
            throw new UnsupportedOperationException();
        return topLeft.add(0, size.height()).subtract(new Size(0, 1));
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

        Rectangle r = of(
                Point.max(a.topLeft, b.topLeft),
                Point.min(a.rightBottom(), b.rightBottom())
        );
        return r.size.isZero() ? null : r;
    }

    public static Rectangle of(Size size) {
        return new Rectangle(ORIGO, size);
    }

    public static Rectangle of(Point topLeft, Point rightBottom) {
        return new Rectangle(topLeft,
                new Size(rightBottom.x() - topLeft.x() + 1, rightBottom.y() - topLeft.y() + 1));
    }

    @Override
    public Rectangle bounds() {
        return this;
    }

    @Override
    public Rectangle translate(Point point) {
        return new Rectangle(topLeft.add(point), size);
    }

    public Rectangle centered(Size size) {
        // (size() - size) / 2
        return new Rectangle(topLeft.add(size().asPoint().subtract(size).divide(2)), size);
    }

    public Rectangle withSize(Size size) {
        return new Rectangle(topLeft, size);
    }

    public Rectangle withInsets(int top, int right, int bottom, int left) {
        return new Rectangle(topLeft.add(new Size(left, top)), size.subtract(new Size(left + right, top + bottom)));
    }

    public Rectangle atOrigo() {
        return new Rectangle(ORIGO, size);
    }

    public Rectangle withInnerInsets(Insets i) {
        return withInsets(i.top(), i.right(), i.bottom(), i.left());
    }

    public Rectangle withOuterInsets(Insets insets) {
        return new Rectangle(
                topLeft.subtract(new Size(insets.left(), insets.top())),
                size.add(new Size(insets.left() + insets.right(), insets.top() + insets.bottom()))
        );
    }

    @Override
    public Shape intoBounds(Rectangle bounds) {
        return bounds;
    }

    public int area() {
        return size().width() * size.height();
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

    //@Override
    //public Rectangle bounds() {
//        return this;
//    }


    @Override
    public List<Path> outlines() {
        return List.of(new Polyline(List.of(rightTop(), rightBottom(), leftBottom(), topLeft())));
    }

    @Override
    public Shape unionWith(Shape other) {
        if (isEmpty())
            return other;

        if (!(other instanceof Rectangle))
            return Shape.super.unionWith(other);

        return union(this, (Rectangle) other);
    }

    @Override
    public Shape intersectionWith(Shape other) {
        if (!(other instanceof Rectangle))
            throw new UnsupportedOperationException(); // ???

        return intersection(this, (Rectangle) other);
    }

    public Point center() {
        return topLeft().add(size.divide(2));
    }

    public int left() {
        return topLeft.x();
    }

    public int top() {
        return topLeft.y();
    }

    public int width() {
        return size.width();
    }

    public int height() {
        return size.height();
    }

}
