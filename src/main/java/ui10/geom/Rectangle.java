package ui10.geom;

import ui10.geom.shape.BézierPath;
import ui10.geom.shape.Shape;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static ui10.geom.Point.ORIGO;

public class Rectangle extends Shape {

    private final Point topLeft;
    private final Size size;

    public Rectangle(Point topLeft, ui10.geom.Size size) {
        this.topLeft = topLeft;
        this.size = size;
    }

    public Point topLeft() {
        return topLeft;
    }

    public Size size() {
        return size;
    }

    public Rectangle(int x, int y, int w, int h) {
        this(new Point(x, y), new Size(w, h));
    }

    public boolean isEmpty() {
        return size.width() == 0 || size.height() == 0;
    }

    public Point bottomRight() {
        return topLeft.add(size);
    }

    public Point topRight() {
        return topLeft.add(size.width(), 0);
    }

    public Point bottomLeft() {
        return topLeft.add(0, size.height());
    }

    public static Rectangle union(Rectangle a, Rectangle b) { // ez valójában nem is unió
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

        if (a.right() <= b.left() || b.right() <= a.left() ||
                a.bottom() <= b.top() || b.bottom() <= a.top())
            return null;

        Rectangle r = of(
                Point.max(a.topLeft, b.topLeft),
                Point.min(a.bottomRight(), b.bottomRight())
        );
        assert !r.size.isZero();
        return r;
    }

    public static Rectangle cornerAt(Corner corner, Point p, Size size) {
        return switch (corner) {
            case TOP_LEFT -> new Rectangle(p, size);
            case TOP_RIGHT -> new Rectangle(p.subtract(size.width(), 0), size);
            case BOTTOM_RIGHT -> new Rectangle(p.subtract(size), size);
            case BOTTOM_LEFT -> new Rectangle(p.subtract(0, size.width()), size);
        };
    }

    public static Rectangle of(Size size) {
        return new Rectangle(ORIGO, size);
    }

    public static Rectangle of(Point topLeft, Point rightBottom) {
        return new Rectangle(topLeft,
                new Size(rightBottom.x() - topLeft.x(), rightBottom.y() - topLeft.y()));
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

    public boolean contains(Rectangle rectangle) {
        return rectangle.equals(intersection(this, rectangle));
    }

    //    @Override
    public int containment(Rectangle rectangle) {
        Rectangle r = intersection(this, rectangle);
        if (r == null)
            return 0;
        return r.area();
    }

    //@Override
    //public Rectangle bounds() {
//        return this;
//    }


    @Override
    public List<BézierPath> outlines() {
        return List.of(
                BézierPath.builder().
                        moveTo(topRight()).
                        lineTo(bottomRight()).
                        lineTo(bottomLeft()).
                        lineTo(topLeft()).
                        build()
        );
    }


    @Override
    public void scan(Rectangle clip, Consumer<HLine> consumer) {
        Rectangle r = intersectionWith(clip);
        if (r != null)
            for (int y = 0; y < r.height(); y++)
                consumer.accept(new HLine(r.topLeft().add(0, y), r.width()));
    }

    @Override
    public Shape unionWith(Shape other) {
        if (isEmpty())
            return other;

        if (!(other instanceof Rectangle))
            return super.unionWith(other);

        return union(this, (Rectangle) other);
    }

    @Override
    public Shape intersectionWith(Shape other) {
        if (size.isZero())
            return null;

        if (!(other instanceof Rectangle))
            return other.intersectionWith(this);

        return intersection(this, (Rectangle) other);
    }

    // nullable a result
    public Rectangle intersectionWith(Rectangle other) {
        return intersection(this, other);
    }

    public Point center() {
        return topLeft().add(size.divide(2));
    }

    public int left() {
        return topLeft.x();
    }

    public int right() {
        return topLeft.x() + size.width();
    }

    public int top() {
        return topLeft.y();
    }

    public int bottom() {
        return topLeft.y() + size.height();
    }

    public int width() {
        return size.width();
    }

    public int height() {
        return size.height();
    }

    public Rectangle left(int width) {
        return new Rectangle(topLeft, size.withWidth(width));
    }

    public Rectangle right(int width) {
        return new Rectangle(topRight().addX(-width), size.withWidth(width));
    }

    public Rectangle top(int height) {
        return new Rectangle(topLeft, size.withHeight(height));
    }

    public Rectangle bottom(int height) {
        return new Rectangle(bottomLeft().addY(-height), size.withHeight(height));
    }

    public Point corner(Corner corner) {
        return switch (corner) {
            case TOP_LEFT -> topLeft();
            case TOP_RIGHT -> topRight();
            case BOTTOM_LEFT -> bottomLeft();
            case BOTTOM_RIGHT -> bottomRight();
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return topLeft.equals(rectangle.topLeft) && size.equals(rectangle.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topLeft, size);
    }

    @Override
    public String toString() {
        return "Rect {" + topLeft + " " + size + "}";
    }

    public enum Corner {
        TOP_LEFT, BOTTOM_LEFT,
        BOTTOM_RIGHT, TOP_RIGHT
    }
}
