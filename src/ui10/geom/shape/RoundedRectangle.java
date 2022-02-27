package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;

import java.util.List;

import static java.util.Arrays.stream;
import static ui10.geom.Rectangle.Corner.*;

public class RoundedRectangle extends CompositeShape {

    private final Rectangle rectangle;
    private final int radius;

    public RoundedRectangle(Rectangle rectangle, int radius) {
        super(List.of(
                new RoundedCorner(Rectangle.cornerAt(TOP_LEFT, rectangle.topLeft(), new Size(radius, radius)), TOP_LEFT),
                new RoundedCorner(Rectangle.cornerAt(TOP_RIGHT, rectangle.topRight(), new Size(radius, radius)), TOP_RIGHT),
                new RoundedCorner(Rectangle.cornerAt(BOTTOM_RIGHT, rectangle.bottomRight(), new Size(radius, radius)), BOTTOM_RIGHT),
                new RoundedCorner(Rectangle.cornerAt(BOTTOM_LEFT, rectangle.bottomLeft(), new Size(radius, radius)), BOTTOM_LEFT),
                Rectangle.of(rectangle.topLeft().add(0, radius), rectangle.bottomRight().subtract(0, radius)),
                Rectangle.of(rectangle.topLeft().add(radius, 0), rectangle.topRight().add(-radius, radius)),
                Rectangle.of(rectangle.bottomLeft().add(radius, -radius), rectangle.bottomRight().add(-radius, 0))
        ));
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
        b.lineTo(rectangle.topRight().add(-radius, 0));
        b.quadCurveTo(rectangle.topRight().add(0, radius), rectangle.topRight());
        b.lineTo(rectangle.bottomRight().add(0, -radius));
        b.quadCurveTo(rectangle.bottomRight().add(-radius, 0), rectangle.bottomRight());
        b.lineTo(rectangle.bottomLeft().add(radius, 0));
        b.quadCurveTo(rectangle.bottomLeft().add(0, -radius), rectangle.bottomLeft());
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

    private static class RoundedCorner extends Shape {

        private final Rectangle rectangle;
        private final Rectangle.Corner corner;

        public RoundedCorner(Rectangle rectangle, Rectangle.Corner corner) {
            this.rectangle = rectangle;
            this.corner = corner;
        }

        @Override
        public Rectangle bounds() {
            return rectangle;
        }

        @Override
        public List<BézierPath> outlines() {
            BézierPath.Builder b = BézierPath.builder();
            Point[] corners = stream(Rectangle.Corner.values()).map(rectangle::corner).toArray(Point[]::new);
            int i = corner.ordinal() + 1;
            b.moveTo(corners[i++ % 4]);
            b.lineTo(corners[i++ % 4]);
            b.lineTo(corners[i++ % 4]);
            b.quadCurveTo(corners[(i + 1) % 4], corners[i % 4]);
            return List.of(b.build());
        }

        @Override
        public String toString() {
            return "Rounded "+corner+" corner at "+rectangle;
        }
    }
}
