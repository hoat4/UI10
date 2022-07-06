package ui10.graphics;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.geom.Point;
import ui10.geom.shape.Shape;
import ui10.image.Color;

import java.util.ArrayList;
import java.util.List;

// TODO listener
public class LinearGradient extends Element {

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

    public record Stop(Color color, int pos) {
    }
/*
    public static LinearGradient vertical(Color from, Color to) {
        return new LinearGradient() {
            @Override
            protected void onShapeApplied(Shape shape) {
                stops.clear();
                stops.add(new Stop(from, 0));
                stops.add(new Stop(to, shape.bounds().size().height() - 1));

                start(ORIGO);
                end(new Point(0, shape.bounds().size().height() - 1));
            }
        };
    }
 */
}
