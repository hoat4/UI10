package ui10.decoration;

import ui10.geom.HLine;
import ui10.geom.Insets;
import ui10.geom.Radiuses;
import ui10.geom.Rectangle;
import ui10.geom.shape.BézierPath;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.geom.shape.ShapeOperations;

import java.util.List;
import java.util.function.Consumer;

public class BorderShape extends Shape {

    public final Rectangle rectangle;
    public final Insets insets;
    public final Radiuses radiuses;

    public BorderShape(Rectangle rectangle, Insets insets, Radiuses radiuses) {
        this.rectangle = rectangle;
        this.insets = insets;
        this.radiuses = radiuses;
    }

    public static Shape make(Shape shape, Insets insets, Radiuses radiuses) {
        if (shape instanceof Rectangle r)
            return new BorderShape(r, insets, radiuses);
        else if (shape instanceof RoundedRectangle r) {
            return new BorderShape(r.bounds(), insets, new Radiuses(
                    Math.max(radiuses.topLeft(), r.topLeftRadius), // these maxes are not correct if the insets are enough great
                    Math.max(radiuses.topRight(), r.topRightRadius),
                    Math.max(radiuses.bottomLeft(), r.bottomLeftRadius),
                    Math.max(radiuses.bottomRight(), r.bottomRightRadius)
            ));
        }else{
            if (!radiuses.allZero())
                shape = new RoundedRectangle(shape.bounds(), radiuses).intersectionWith(shape);
            return shape.subtract(insets.removeFrom(shape));
        }
    }

    @Override
    public List<BézierPath> outlines() {
        return List.of(
                new RoundedRectangle(rectangle, radiuses).outlines().get(0),
                new RoundedRectangle(rectangle.withInnerInsets(insets), radiuses).outlines().get(0)
        );
    }

    @Override
    public Rectangle bounds() {
        if (insets.top() != 0) {
            if (insets.right() == 0 && insets.bottom() == 0 && insets.left() == 0)
                return rectangle.top(insets.top() + Math.max(radiuses.topLeft(), radiuses.topRight()));
        } else if (insets.right() != 0) {
            if (insets.bottom() == 0 && insets.left() == 0)
                return rectangle.right(insets.right() + Math.max(radiuses.topRight(), radiuses.bottomRight()));
        } else if (insets.bottom() != 0) {
            if (insets.left() == 0)
                return rectangle.bottom(insets.bottom() + Math.max(radiuses.bottomLeft(), radiuses.bottomRight()));
        } else if (insets.left() != 0)
            return rectangle.left(insets.left() + Math.max(radiuses.topLeft(), radiuses.bottomLeft()));

        return rectangle;
    }

    @Override
    public void scan(Rectangle clip, Consumer<HLine> consumer) {
        throw new UnsupportedOperationException("TODO");
    }
}
