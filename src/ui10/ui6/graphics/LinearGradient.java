package ui10.ui6.graphics;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.ui6.Surface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ui10.geom.Point.ORIGO;

public class LinearGradient extends Surface {

    private Point start, end;
    public final List<Stop> stops = new ArrayList<>();

    public Point start() {
        return start;
    }

    public LinearGradient start(Point start) {
        this.start = start;
        return this;
    }

    public Point end() {
        return end;
    }

    public LinearGradient end(Point end) {
        this.end = end;
        return this;
    }

    @Override
    public Shape computeShape(BoxConstraints constraints) {
        return Rectangle.of(constraints.min());
    }

    public record Stop(Color color, int pos) {
    }

    public static LinearGradient vertical(Color from, Color to) {
        return new LinearGradient() {
            @Override
            protected void applyShapeImpl(Shape shape, Consumer<Surface> layoutContext) {
                stops.clear();
                stops.add(new Stop(from, 0));
                stops.add(new Stop(to, shape.bounds().size().height() - 1));

                start(ORIGO);
                end(new Point(0, shape.bounds().size().height() - 1));
            }
        };
    }
}
