package ui10.geom.shape;

import java.util.List;

public record CompositePath<E extends Path>(List<? extends E> elements) implements Path {

    @Override
    public void iterate(PathConsumer consumer) {
        for (E p : elements) {
            consumer.addSubpath(p);
        }
    }
}
