package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class RoundedRectangle extends Shape {

    private final Rectangle rectangle;
    private final int radius;

    public RoundedRectangle(Rectangle rectangle, int radius) {
        this.rectangle = rectangle;
        this.radius = radius;
    }

    public Rectangle rectangle() {
        return rectangle;
    }

    public int radius() {
        return radius;
    }

    @Override
    public Rectangle bounds() {
        return rectangle;
    }

    @Override
    public List<BézierPath> outlines() {
        BézierPath.Builder b = BézierPath.builder();
        b.moveTo(rectangle.topLeft().add(radius, 0));
        b.lineTo(rectangle.rightTop().add(-radius, 0));
        b.quadCurveTo(rectangle.rightTop().add(0, radius), rectangle.rightTop());
        b.lineTo(rectangle.rightBottom().add(0, -radius));
        b.quadCurveTo(rectangle.rightBottom().add(-radius, 0), rectangle.rightBottom());
        b.lineTo(rectangle.leftBottom().add(radius, 0));
        b.quadCurveTo(rectangle.leftBottom().add(0, -radius), rectangle.leftBottom());
        b.lineTo(rectangle.topLeft().add(0, radius));
        b.quadCurveTo(rectangle.topLeft().add(radius, 0), rectangle.topLeft());
        b.close();
        return List.of(b.build());
    }

    @Override
    public Shape translate(Point point) {
        return new RoundedRectangle(rectangle.translate(point), radius);
    }

    @Override
    public String toString() {
        return "RoundedRectangle{" +
                "rectangle=" + rectangle +
                ", radius=" + radius +
                '}';
    }
}
