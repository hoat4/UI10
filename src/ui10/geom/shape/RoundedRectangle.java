package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;

public record RoundedRectangle(Rectangle rectangle, int radius /*Fraction?*/) implements Shape {

    @Override
    public Rectangle bounds() {
        return rectangle;
    }

    @Override
    public List<Path> outlines() {
        List<StandardPathElement> p = new ArrayList<>();
        p.add(new StandardPathElement.MoveTo(rectangle.topLeft().add(radius, 0)));
        p.add(new StandardPathElement.LineTo(rectangle.rightTop().add(-radius, 0)));
        p.add(new StandardPathElement.QuadCurveTo(rectangle.rightTop().add(0, radius), rectangle.rightTop()));
        p.add(new StandardPathElement.LineTo(rectangle.rightBottom().add(0, -radius)));
        p.add(new StandardPathElement.QuadCurveTo(rectangle.rightBottom().add(-radius, 0), rectangle.rightBottom()));
        p.add(new StandardPathElement.LineTo(rectangle.leftBottom().add(radius, 0)));
        p.add(new StandardPathElement.QuadCurveTo(rectangle.leftBottom().add(0, -radius), rectangle.leftBottom()));
        p.add(new StandardPathElement.LineTo(rectangle.topLeft().add(0, radius)));
        p.add(new StandardPathElement.QuadCurveTo(rectangle.topLeft().add(radius, 0), rectangle.topLeft()));
        p.add(new StandardPathElement.Close());
        return List.of(new CompositePath<>(p));
    }

    @Override
    public Shape translate(Point point) {
        return new RoundedRectangle(rectangle.translate(point), radius);
    }

    @Override
    public Shape unionWith(Shape other) {
        return null;
    }

    @Override
    public Shape intersectionWith(Shape other) {
        return null;
    }
}
