package ui10.geom.shape;

import ui10.geom.Point;

import java.util.List;

public record Polyline(List<Point> points) implements Path {

    @Override
    public void iterate(PathConsumer consumer) {
        points.forEach(consumer::addPoint);
    }
}
