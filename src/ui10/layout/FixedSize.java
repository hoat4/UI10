package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

import java.util.Collection;

public class FixedSize extends WrapperPane {

    public final ScalarProperty<Size> size = ScalarProperty.create();

    public FixedSize() {
    }

    public FixedSize(Node content, Size size) {
        super(content);
        this.size.set(size);
    }

    public FixedSize(ObservableScalar<Node> content, ObservableScalar<Size> size) {
        super(content);
        this.size.bindTo(size);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return new Layout(content) {

            {
                dependsOn( size);
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                Size s = size.get();
                if (!constraints.contains(s))
                    throw new IllegalStateException("fixed size "+s+" doesn't fits in " + constraints + " (content: " + content.get() + ")");
                return s;
            }

            @Override
            protected void layout(Collection<?> updatedChildren) {
                content.get().bounds.set(Rectangle.of(bounds.get().size()));
            }
        }.asNodeObservable();
    }
}
