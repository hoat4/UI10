package ui10.geom.shape;

import ui10.geom.Point;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public record Polyline(List<Point> points) implements Path {

    @Override
    public void iterate(PathConsumer consumer) {
        points.forEach(consumer::addPoint);
    }

    @Override
    public Path transform(UnaryOperator<Point> op) {
        return new Polyline(points.stream().map(op).collect(Collectors.toList()));
    }
}
