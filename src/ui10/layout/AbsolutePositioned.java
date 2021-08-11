package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

import java.util.Collection;

public class AbsolutePositioned extends WrapperPane {

    public final ScalarProperty<Point> position = ScalarProperty.create("AbsolutePositioned.position");

    public AbsolutePositioned() {
    }

    public AbsolutePositioned(Node content, Point position) {
        super(content);
        this.position.set(position);
    }

    public AbsolutePositioned(ObservableScalar<Node> content, ObservableScalar<Point> position) {
        super(content);
        this.position.bindTo(position);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return new Layout(content) {

            {
                dependsOn(position);
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                Point pos = position.get();
                if (!constraints.contains(Size.of(pos)))
                    throw new IllegalStateException("position " + pos + " doesn't conform to " + constraints + " (content: " + content.get() + ")");
                return content.get().determineSize(constraints.subtract(pos)).add(pos);
            }

            @Override
            protected void layout(Collection<?> updatedChildren) {
                Size maxSize = bounds.get().size().subtract(position.get());
                Size size = content.get().determineSize(new BoxConstraints(Size.ZERO, maxSize));
                content.get().bounds.set(new Rectangle(position.get(), size));
            }
        }.asNodeObservable();
    }
}
