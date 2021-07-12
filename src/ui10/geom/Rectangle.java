package ui10.geom;

public record Rectangle(Point topLeft, Point rightBottom) {

    public Size size() {
        return new Size(rightBottom.x().sub(topLeft.x()),
                rightBottom.y().sub(topLeft.y()), rightBottom.z().sub(topLeft.z()));
    }

    public static Rectangle rect(Point a, Point b) {
        // TODO pontok cseréje, ha szüksges
        return new Rectangle(a, b);
    }
}
