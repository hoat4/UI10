package ui10.geom.shape;

import ui10.geom.*;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static ui10.geom.Rectangle.Corner.*;

public class RoundedRectangle extends CompositeShape {

    private final Rectangle rectangle;
    public final int topLeftRadius;
    public final int topRightRadius;
    public final int bottomLeftRadius;
    public final int bottomRightRadius;

    public RoundedRectangle(Rectangle rectangle, int radius) {
        this(rectangle, radius, radius, radius, radius);
    }

    public RoundedRectangle(Rectangle rectangle, Radiuses radiuses) {
        this(rectangle, radiuses.topLeft(), radiuses.topRight(), radiuses.bottomLeft(), radiuses.bottomRight());
    }

    public RoundedRectangle(Rectangle rectangle, int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        this.rectangle = rectangle;
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
    }

    public static Shape make(Rectangle rectangle, Radiuses radiuses) {
        if (radiuses.allZero())
            return rectangle;
        else
            return new RoundedRectangle(rectangle, radiuses);
    }

    @Override
    protected List<? extends Shape> makeSubshapes() {
        Thread.dumpStack();
        int top = Math.max(topLeftRadius, topRightRadius);
        int bottom = Math.max(bottomLeftRadius, bottomRightRadius);

        return List.of(
                new RoundedCorner(Rectangle.cornerAt(TOP_LEFT, rectangle.topLeft(), new Size(topLeftRadius, topLeftRadius)), TOP_LEFT),
                new RoundedCorner(Rectangle.cornerAt(TOP_RIGHT, rectangle.topRight(), new Size(topRightRadius, topRightRadius)), TOP_RIGHT),
                new RoundedCorner(Rectangle.cornerAt(BOTTOM_RIGHT, rectangle.bottomRight(), new Size(bottomLeftRadius, bottomLeftRadius)), BOTTOM_RIGHT),
                new RoundedCorner(Rectangle.cornerAt(BOTTOM_LEFT, rectangle.bottomLeft(), new Size(bottomRightRadius, bottomRightRadius)), BOTTOM_LEFT),

                Rectangle.of(rectangle.topLeft().add(0, top), rectangle.bottomRight().subtract(0, bottom)),
                Rectangle.of(rectangle.topLeft().add(topLeftRadius, 0), rectangle.topRight().add(-topRightRadius, top)),
                Rectangle.of(rectangle.bottomLeft().add(bottomLeftRadius, -bottom), rectangle.bottomRight().add(-bottomRightRadius, 0))

                // TODO ha a felső vagy alsó részen az egyik corner kisebb mint a másik és a másik nem 0, akkor kimarad
                //      az egyik corner alatt egy kis hely, azt pótolni kéne
        );
    }

    public Rectangle rectangle() {
        return rectangle;
    }

    @Override
    public Rectangle bounds() {
        return rectangle;
    }

    @Override
    public Shape subtract(Shape other) {
        Rectangle b = other.bounds();
        for (Shape shape : this.shapes())
            if (shape instanceof RoundedCorner && shape.bounds().intersectionWith(b) != null)
                return ShapeOperations.subtract(this, other);
        return super.subtract(other);
    }

    @Override
    public List<BézierPath> outlines() {
        BézierPath.Builder b = BézierPath.builder();
        b.moveTo(rectangle.topLeft().add(topLeftRadius, 0));
        b.lineTo(rectangle.topRight().add(-topRightRadius, 0));
        b.quadCurveTo(rectangle.topRight().add(0, topRightRadius), rectangle.topRight());
        b.lineTo(rectangle.bottomRight().add(0, -bottomRightRadius));
        b.quadCurveTo(rectangle.bottomRight().add(-bottomRightRadius, 0), rectangle.bottomRight());
        b.lineTo(rectangle.bottomLeft().add(bottomLeftRadius, 0));
        b.quadCurveTo(rectangle.bottomLeft().add(0, -bottomLeftRadius), rectangle.bottomLeft());
        b.lineTo(rectangle.topLeft().add(0, topLeftRadius));
        b.quadCurveTo(rectangle.topLeft().add(topLeftRadius, 0), rectangle.topLeft());
        b.close();
        return List.of(b.build());
    }

    @Override
    public Shape translate(Point point) {
        return new RoundedRectangle(rectangle.translate(point), topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
    }

    @Override
    public String toString() {
        return "RoundedRectangle{" +
                "rectangle=" + rectangle +
                ", radius=" + topLeftRadius + "/" + topRightRadius + "/" + bottomLeftRadius + "/" + bottomRightRadius +
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
        public void scan(Rectangle clip, Consumer<HLine> consumer) {
            Rectangle r = clip.intersectionWith(rectangle);
            if (r == null)
                return;

            for (int y = r.top(); y < r.bottom(); y++) {
                int radius = rectangle.height();
                int x2 = switch (corner) { // [1, r]
                    case TOP_LEFT, TOP_RIGHT -> {
                        int y2 = radius - (y - rectangle.top()) - 1; // [0, r-1]
                        yield (int) (Math.sqrt(radius * radius - y2 * y2));
                    }
                    case BOTTOM_LEFT, BOTTOM_RIGHT -> {
                        int y2 = y - rectangle.top(); // [0, r-1]
                        yield (int) (radius - Math.sqrt(radius * radius - y2 * y2));
                    }
                };
                HLine hLine = switch (corner) {
                    case TOP_LEFT -> new HLine(y, rectangle.right() - x2 - 1, rectangle.right());
                    case TOP_RIGHT -> new HLine(y, rectangle.left(), rectangle.left() + x2 + 1);
                    case BOTTOM_LEFT -> new HLine(y, rectangle.left() + x2, rectangle.right());
                    case BOTTOM_RIGHT -> new HLine(y, rectangle.left(), rectangle.right() - x2);
                };
                hLine = hLine.clip(r);
                if (hLine != null)
                    consumer.accept(hLine);
            }
        }

        @Override
        public String toString() {
            return "Rounded " + corner + " corner at " + rectangle;
        }
    }
}
