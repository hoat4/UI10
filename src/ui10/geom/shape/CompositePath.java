package ui10.geom.shape;

import ui10.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public record CompositePath<E extends Path>(List<? extends E> elements) implements Path {

    @Override
    public void iterate(PathConsumer consumer) {
        for (E p : elements) {
            consumer.addSubpath(p);
        }
    }

    @Override
    public CompositePath<?> transform(UnaryOperator<Point> op) {
        List<Path> l = new ArrayList<>();
        for (E  e : elements)
            l.add(e.transform(op));
        return new CompositePath<>(l);
    }


}
