package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

import java.util.Collection;

public class Bounded extends WrapperPane {

    // ennek lehet hogy más név kéne, hogy megkülönböztessük Node.boundstól
    // vagy lehet hogy inkább annak kéne másik név
    public final ScalarProperty<Rectangle> bounds = ScalarProperty.create("Bounded.bounds");

    public Bounded() {
    }

    public Bounded(Node content, Rectangle bounds) {
        super(content);
        this.bounds.set(bounds);
    }

    public Bounded(ObservableScalar<Node> content, ObservableScalar<Rectangle> bounds) {
        super(content);
        this.bounds.bindTo(bounds);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return new Layout(content) {

            {
                dependsOn(Bounded.this.bounds);
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                Size size = Size.of(Bounded.this.bounds.get().rightBottom());
                if (!constraints.contains(size))
                    throw new IllegalStateException("bounds " + Bounded.this.bounds + " doesn't fit into " +
                            constraints + " (content: " + content.get() + ")");
                return size;
            }

            @Override
            protected void layout(Collection<?> updatedChildren) {
                content.get().bounds.set(Bounded.this.bounds.get());
            }
        }.asNodeObservable();
    }
}
