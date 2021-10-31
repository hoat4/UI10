package ui10.geom;


public record Point(int x, int y) {

    public static final Point ORIGO = new Point(0, 0);

    public static Point min(Point a, Point b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new Point(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }

    public static Point max(Point a, Point b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new Point(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }

    public static Point of(Size size) {
        return new Point(size.width(), size.height());
    }

    public static int distance(Point a, Point b) {
        // int sqrt?
        return (int) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public Point add(Size s) {
        return new Point(x + s.width(), y + s.height());
    }

    public Point add(Point s) {
        return new Point(x + s.x(), y + s.y());
    }

    public Point add(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    public Point subtract(Size s) {
        return new Point(x - s.width(), y - s.height());
    }

    public Point subtract(int dx, int dy) {
        return new Point(x - dx, y - dy);
    }

    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public Point multiply(int i) {
        return new Point(x*i, y*i);
    }

    public Point divide(int num) {
        return new Point((x+num-1)/ num, (y+num-1) / num);
    }

    public Point negate() {
        return new Point(-x, -y);
    }

}
