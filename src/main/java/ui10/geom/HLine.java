package ui10.geom;

// ezt milyen package-be kéne rakni?
public record HLine(Point origin, int width) {

    public HLine {
        if (width <= 0)
            throw new IllegalArgumentException("width of scanline must be positive, but it is " + width + " (origin: " + origin + ")");
    }

    public HLine(Point left, Point right) {
        this(left, right.x() - left.x());
        if (left.y() != right.y())
            throw new IllegalArgumentException("Y coordinates must be equal: " + left + ", " + right);
    }


    public HLine(int y, int left, int right) {
        this(new Point(left, y), right-left);
    }

    /**
     * @return the clipped scanline or {@code null}
     */
    public HLine clip(Rectangle r) {
        if (origin.y() < r.top() || origin.y() >= r.bottom() || origin.x() + width < r.left() || origin.x() >= r.right())
            return null;

        if (origin.x() < r.left())
            return new HLine(origin.withX(r.left()), width + origin.x() - r.left());

        if (origin.x() + width >= r.right())
            return new HLine(origin, r.right() - origin.x());

        return this;
    }

    public int left() {
        return origin.x();
    }

    public int right() {
        return origin.x() + width;
    }

    public int y() {
        return origin.y();
    }

    public Point rightPoint() {
        return origin.add(width, 0);
    }
}