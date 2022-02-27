package ui10.geom;

import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;

public record Insets(int top, int right, int bottom, int left) {

    public Insets(int all) {
        this(all, all);
    }

    public Insets(int topBottom, int leftRight) {
        this(topBottom, leftRight, topBottom, leftRight);
    }

    public int horizontal() {
        return left + right;
    }

    public int vertical() {
        return top + bottom;
    }

    public Size all() {
        return new Size(left + right, top + bottom);
    }

    public Point topLeft() {
        return new Point(left, top);
    }

    public Shape addTo(Shape shape) {
        return shape.intoBounds(shape.bounds().withOuterInsets(this));
    }

    public Shape removeFrom(Shape shape) {
        if (shape instanceof RoundedRectangle r)
            return new RoundedRectangle(r.rectangle().withInnerInsets(this), r.radius()); // radiust csökkenteni kéne
        return shape.intoBounds(shape.bounds().withInnerInsets(this)); // ez nem jó, torzít
    }
}
